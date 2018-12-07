package id.co.reich.mockupsouthscape.async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import id.co.reich.mockupsouthscape.rest.ApiClient;
import id.co.reich.mockupsouthscape.rest.ApiInterface;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class LoginTask extends AsyncTask<Void, Void, Boolean> {
    private OnTaskCompleted listener;
    private final String mEmail;
    private final String mPassword;
    private ApiInterface mApiService;
    private String TAG = this.getClass().getSimpleName();
    private String token;

    public LoginTask(OnTaskCompleted listener, String email, String password)
    {
        this.listener = listener;
        this.mEmail = email;
        this.mPassword = password;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        token = null;

        // synchronous Retrofit API calling inside asynchronous block
        mApiService = ApiClient.getClient().create(ApiInterface.class);
        try {
            Response<ResponseBody> response = mApiService.login(mEmail, mPassword).execute();
            if (response.isSuccessful())
            {
                try {
                    JSONObject jsonRESULTS = new JSONObject(response.body().string());
                    token = jsonRESULTS.get("token").toString();

                    Log.d(TAG, token);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return token!=null;
    }

    @Override
    protected void onPostExecute(final Boolean success){
        if (success)
            listener.onTaskCompleted(token);
        else
            listener.onTaskCompleted("FAILED");
    }

}
