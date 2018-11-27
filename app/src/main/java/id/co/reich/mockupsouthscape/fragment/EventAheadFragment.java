package id.co.reich.mockupsouthscape.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mindorks.placeholderview.InfinitePlaceHolderView;

import id.co.reich.mockupsouthscape.R;
import id.co.reich.mockupsouthscape.pojo.EventList;
import id.co.reich.mockupsouthscape.rest.ApiClient;
import id.co.reich.mockupsouthscape.rest.ApiInterface;
import id.co.reich.mockupsouthscape.view.ItemViewEvent;
import id.co.reich.mockupsouthscape.view.LoadMoreViewEvent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventAheadFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventAheadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventAheadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private InfinitePlaceHolderView mLoadMoreView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EventAheadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EventAheadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventAheadFragment newInstance() {
        EventAheadFragment fragment = new EventAheadFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_ahead, container, false);
        mLoadMoreView = view.findViewById(R.id.loadMore_Event);

        SetupView();

        return view;
    }

    private void SetupView()
    {
        ApiInterface mApiService = ApiClient.getClient().create(ApiInterface.class);
        Call<EventList> call = mApiService.getEvents(0, LoadMoreViewEvent.LOAD_VIEW_SET_COUNT);

        call.enqueue(new Callback<EventList>() {
            @Override
            public void onResponse(Call<EventList> call, Response<EventList> response) {
                if (response.isSuccessful())
                {
                    Log.d(this.getClass().getSimpleName(), "Response Successful");
                    for (int i=0; i<response.body().getEventArrayList().size(); i++)
                    {
                        mLoadMoreView.addView(new ItemViewEvent(mLoadMoreView.getContext(), response.body().getEventArrayList().get(i)));
                    }
                }
            }

            @Override
            public void onFailure(Call<EventList> call, Throwable t) {
                Log.e(this.getClass().getSimpleName(), t.getMessage());
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}