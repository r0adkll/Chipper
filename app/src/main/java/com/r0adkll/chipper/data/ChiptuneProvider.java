package com.r0adkll.chipper.data;

import com.ftinc.kit.util.RxUtils;
import com.ftinc.kit.util.Utils;
import com.r0adkll.chipper.api.ChipperService;
import com.r0adkll.chipper.data.model.Chiptune;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ollie.Ollie;
import ollie.query.Select;
import rx.Observable;

/**
 * This class will be used to inject a easy helper that dynamically loads the master list of
 * chiptunes in this order:
 *
 * • Memory
 * • Disk
 * • Server
 *
 * Created by r0adkll on 11/16/14.
 */
@Singleton
public class ChiptuneProvider {

    /**
     * The list of chiptunes to provide to the UI
     */
    private List<Chiptune> mChiptunes;
    private Map<String, Chiptune> mChiptuneMap;
    private ChipperService mService;

    /**
     * Constructor
     * @param service
     */
    @Inject
    public ChiptuneProvider(ChipperService service){
        mChiptuneMap = new HashMap<>();
        mService = service;
    }

    /**
     * Get a chiptune for a specified id, or null
     *
     * @param id        the chiptune id (server side generated token)
     * @return          The observable for said chiptune
     */
    public Observable<Chiptune> chiptune(String id){
        return chiptunes().map(chiptunes -> mChiptuneMap.get(id));
//        return chiptunes().flatMap(chiptunes -> Observable.from(chiptunes))
//                .filter(chiptune -> chiptune.chiptuneId.equals(id));
    }

    /**
     * Dynamically load a random chiptune. Instantly if the chiptunes are already loaded into memory
     * but load them from the db if not, and if not in the database, load them from the server
     */
    public Observable<Chiptune> randomChiptune(){
        return chiptunes()
                .map(chiptunes -> {
                    int index = Utils.getRandom().nextInt(mChiptunes.size());
                    return mChiptunes.get(index);
                });
    }

    /***********************************************************************************************
     *
     * Observable Methods
     *
     */

    /**
     * Using some RxMagic intelligently load the chiptunes from the path of least resistance.
     * First, checking in memory. Second, checking on the disk. Lastly, checking the network
     * caching and saving at the appropriate stages to finally deliver the <i>first</i> available
     * source to the caller.
     */
    public Observable<List<Chiptune>> chiptunes(){
        return Observable.concat(memory(), disk(), network())
                .compose(RxUtils.<List<Chiptune>>applyIOSchedulers())
                .first();
    }

    /**
     * The observable to return the list of chiptunes stored in memory
     *
     * @return      the observable for the chiptunes in memory
     */
    private Observable<List<Chiptune>> memory(){
        return Observable.just(mChiptunes);
    }

    /**
     * The observable to return the list of chiptunes saved on disk
     * then cache them into memory
     *
     * @return      the observable for the list of chiptunes
     */
    private Observable<List<Chiptune>> disk(){
        return Select.from(Chiptune.class)
                .observable()
                .doOnNext(chiptunes -> cacheInMemory(chiptunes));
    }

    /**
     * The observable to return the list of chiptunes from the server
     * then storing them to disk and caching in memory
     *
     * @return      the observable for the list of chiptunes
     */
    private Observable<List<Chiptune>> network(){
        return mService.getChiptunes()
                .doOnNext(chiptunes -> {
                    saveToDisk(chiptunes);
                    cacheInMemory(chiptunes);
                });
    }

    /***********************************************************************************************
     *
     * Storing Helper Methods
     *
     */

    /**
     * Cache teh list of chiptunes into memory
     *
     * @param chiptunes     the list of chiptunes from disk or network to cache in memory
     */
    private void cacheInMemory(List<Chiptune> chiptunes){
        mChiptunes = Collections.unmodifiableList(chiptunes);
        mChiptuneMap = new HashMap<>();

        // Map for super fast memory retrieval
        for (Chiptune chiptune : mChiptunes) {
            mChiptuneMap.put(chiptune.chiptuneId, chiptune);
        }
    }

    /**
     * Save the list of *fresh* chiptunes to the disk
     *
     * @param chiptunes the list of chiptunes fresh from the network to be saved to disk
     */
    private void saveToDisk(List<Chiptune> chiptunes){
        // Save all the chiptunes
        Ollie.getDatabase().beginTransaction();
        try{
            for(Chiptune chiptune: chiptunes){
                chiptune.save();
            }
            Ollie.getDatabase().setTransactionSuccessful();
        } finally{
            Ollie.getDatabase().endTransaction();
        }
    }

}
