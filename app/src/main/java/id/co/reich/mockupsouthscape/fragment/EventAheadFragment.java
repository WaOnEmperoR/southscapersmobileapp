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

import butterknife.ButterKnife;
import butterknife.Unbinder;
import id.co.reich.mockupsouthscape.R;
import id.co.reich.mockupsouthscape.pojo.Event;
import id.co.reich.mockupsouthscape.pojo.EventList;
import id.co.reich.mockupsouthscape.pojo.UserDetail;
import id.co.reich.mockupsouthscape.rest.ApiClient;
import id.co.reich.mockupsouthscape.rest.ApiInterface;
import id.co.reich.mockupsouthscape.view.ItemViewEventAhead;
import id.co.reich.mockupsouthscape.view.LoadMoreViewEventAhead;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
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

    private CompositeDisposable disposable = new CompositeDisposable();
    private Unbinder unbinder;
    private View mProgressView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        mLoadMoreView = view.findViewById(R.id.loadMore_EventAhead);
        mProgressView = view.findViewById(R.id.event_ahead_progress);

        unbinder = ButterKnife.bind(this.getActivity());

        Log.d(this.getClass().getSimpleName(), "onCreateView");
        SetupView();

        return view;
    }

    private void SetupView()
    {
        Log.d(this.getClass().getSimpleName(), "SetupView");

        mProgressView.setVisibility(View.VISIBLE);

        Observable<EventList> eventsAheadObservable = getEventAheadList();

        disposable.add(
            eventsAheadObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<EventList>() {
                        @Override
                        public void onNext(EventList eventList) {
                            Log.d(this.getClass().getSimpleName(), "Response Successful");

                            for (int i=0; i<eventList.getEventArrayList().size(); i++)
                            {
                                mLoadMoreView.addView(new ItemViewEventAhead(getActivity(), eventList.getEventArrayList().get(i)));
                            }

                            mProgressView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(this.getClass().getSimpleName(), e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.e(this.getClass().getSimpleName(), "onComplete from RXJava");
                            this.dispose();
                        }
                    })
        );

        mLoadMoreView.setLoadMoreResolver(new LoadMoreViewEventAhead(mLoadMoreView));

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private Observable<EventList> getEventAheadList()
    {
        final ApiInterface mApiService = ApiClient.getClient().create(ApiInterface.class);
        return mApiService.RxGetEvents(0, LoadMoreViewEventAhead.LOAD_VIEW_SET_COUNT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
