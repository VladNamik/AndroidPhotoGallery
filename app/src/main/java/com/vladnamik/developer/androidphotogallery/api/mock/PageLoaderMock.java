package com.vladnamik.developer.androidphotogallery.api.mock;

import android.util.Log;

import com.vladnamik.developer.androidphotogallery.activities.PageFragment;
import com.vladnamik.developer.androidphotogallery.api.PageLoader;
import com.vladnamik.developer.androidphotogallery.api.entities.Page;
import com.vladnamik.developer.androidphotogallery.api.entities.Photo;
import com.vladnamik.developer.androidphotogallery.api.px500.ImageAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PageLoaderMock implements PageLoader{
    private static final String PAGE_LOADER_LOG_TAG = "PageLoader";

    private static final List<Photo> mockPhotos = new ArrayList<Photo>(){
        {
            add(new Photo("Closing time", "https://drscdn.500px.org/photo/261893927/q%3D80_h%3D300/v2?webp=true&sig=71dfdc8997038cd645c67aa841b07d396d740786f9b31253dc64c396a22e28f7", "webp", "photo/261893927/market-after-closing-time-by-alexander-sheffer"));
            add(new Photo("World heritage dolomites", "https://drscdn.500px.org/photo/261893819/q%3D80_h%3D300/v2?webp=true&sig=77f93c933c9254d34b1259974b9c6f7a14ddd6a2052696ce1a28f84e87cf6a96", "webp", "photo/261893819/world-heritage-dolomites-by-tom-engl"));
            add(new Photo("Resting", "https://drscdn.500px.org/photo/261893751/q%3D80_h%3D300/v2?webp=true&sig=982f6b8d0717e8c20ef1543f6122aa3d87fae8cab1b854114ecde976f94edb78", "webp", "photo/261893751/resting-by-daniel-painter"));
            add(new Photo("Untitled", "https://drscdn.500px.org/photo/261893933/q%3D80_h%3D450/v2?webp=true&sig=69c1d5b2527ece6b99a20afba0532db9eefc012a9affde8fa11fb8dff7a08ba7", "webp", "photo/261893933/untitled-by-nazrul-islam-rimon"));
            add(new Photo("Untitled", "https://drscdn.500px.org/photo/261893929/q%3D80_h%3D450/v2?webp=true&sig=b05fad2e23a91b20fefb565ea278d827e1e0fbc15720b7bb5c34c581cf774998", "webp", "photo/261893929/untitled-by-dlan"));
            add(new Photo("Grasshopper", "https://drscdn.500px.org/photo/261893753/q%3D80_h%3D450/v2?webp=true&sig=15e543aeb19245c76cdcea31782f9335f9d78c17c802ba7a76c351a20ce7b656", "webp", "photo/261893753/grasshopper-by-sadet-uslu"));
            add(new Photo("Roller in flight", "https://drscdn.500px.org/photo/261893735/q%3D80_h%3D300/v2?webp=true&sig=a26ba49d0759018340a80828a89573cc6a29846d3b642ed0d7d7d4ce45f33a03", "webp", "photo/261893735/roller-in-flight-by-pascal-de-munck"));
            add(new Photo("Untitled", "https://drscdn.500px.org/photo/261893915/q%3D80_h%3D300/v2?webp=true&sig=51665637f1f38ce9fc9e7fa5acc1a92c900ca82a411b61c6e55e0c0b08504782", "webp", "photo/261893915/untitled-by-sotheaveas"));
            add(new Photo("Sassari in fiore", "https://drscdn.500px.org/photo/261893533/q%3D80_h%3D300/v2?webp=true&sig=b21b7879228792224176e5acfa241c6428186a6318048972e994cfa69a9dfdcb", "webp", "photo/261893533/sassari-in-fiore-2018-by-mauro-nuvoli"));
            add(new Photo("The clouds", "https://drscdn.500px.org/photo/261893453/q%3D80_h%3D300/v2?webp=true&sig=921198fa156b6f70567a113b1b3dcf0661f7c3f77d2d3cccb925048d63e2a6f5", "webp", "photo/261893453/the-clouds-by-mauro-nuvoli"));
        }
    };

    private final PageFragment.PageCallback pageCallback;

    public PageLoaderMock(PageFragment.PageCallback pageCallback) {
        this.pageCallback = pageCallback;
    }

    public void loadPage(int pageNumber) {
        Random random = new Random(pageNumber);
        Log.d(PAGE_LOADER_LOG_TAG, "Forming mock for page #" + pageNumber);

        List<Photo> photos = new ArrayList<>(10);
        for (int i = 0; i < 10; i++)
        {
            photos.add(mockPhotos.get(Math.abs(random.nextInt()) % mockPhotos.size()));
        }

        pageCallback.onResponse(photos);
    }
}
