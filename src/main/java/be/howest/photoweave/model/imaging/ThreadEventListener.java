package be.howest.photoweave.model.imaging;

/**
 * Created by tomdo on 10/11/2017.
 */
public interface ThreadEventListener {
    void OnRedrawBegin();
    void onThreadComplete();
    void onRedrawComplete();
}