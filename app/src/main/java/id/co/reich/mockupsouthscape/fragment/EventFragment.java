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
import id.co.reich.mockupsouthscape.pojo.EventList;
import id.co.reich.mockupsouthscape.rest.ApiClient;
import id.co.reich.mockupsouthscape.rest.ApiInterface;
import id.co.reich.mockupsouthscape.view.ItemViewEvent;
import id.co.reich.mockupsouthscape.view.LoadMoreViewEvent;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM3 = "event_choice";
    private InfinitePlaceHolderView mLoadMoreView;
    // choice == 1 -> event ahead
    // choice == 2 -> event completed
    private int mChoice;

    private CompositeDisposable disposable = new CompositeDisposable();
    private Unbinder unbinder;
    private View mProgressView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventFragment newInstance() {
        EventFragment fragment = new EventFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChoice = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        mLoadMoreView = view.findViewById(R.id.loadMore_Event);
        mProgressView = view.findViewById(R.id.event_progress);

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
                                mLoadMoreView.addView(new ItemViewEvent(getActivity(), eventList.getEventArrayList().get(i)));
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

        mLoadMoreView.setLoadMoreResolver(new LoadMoreViewEvent(mLoadMoreView, mChoice));

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
        return mApiService.RxGetEvents(0, LoadMoreViewEvent.LOAD_VIEW_SET_COUNT, mChoice)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
