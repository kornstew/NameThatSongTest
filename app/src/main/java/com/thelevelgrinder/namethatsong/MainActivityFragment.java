package com.thelevelgrinder.namethatsong;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainActivityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainActivityFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int SONGS_IN_QUIZ = 10; // songs
    // TODO: Rename and change types of parameters
    private List<String> songNameList;
    private List<String> songList;
    private int totalGuesses;
    private int guessRows; // number of rows displaying guess Buttons
    private int correctAnswers;
    private String correctAnswer;
    private SecureRandom random;
    private Handler handler;
    private LinearLayout quizLinearLayout;
    private ImageView songImageView; // this is were
    private TextView answerTextView;
    private TextView questionNumberTextView;
    private LinearLayout[] guessLinearLayouts;

    private OnFragmentInteractionListener mListener;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainActivityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainActivityFragment newInstance(String param1, String param2) {
        MainActivityFragment fragment = new MainActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        songNameList =  new ArrayList<>();
        songList= new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();
        //


        // reference to GUI components
        quizLinearLayout = (LinearLayout)view.findViewById(R.id.quizLinearLayout);
        questionNumberTextView = (TextView)view.findViewById(R.id.questionNumberTextView);
        // get references to GUI components
        quizLinearLayout =
                (LinearLayout) view.findViewById(R.id.quizLinearLayout);
        questionNumberTextView =
                (TextView) view.findViewById(R.id.questionNumberTextView);
       //   flagImageView = (ImageView) view.findViewById(R.id.flagImageView);
        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] =
                (LinearLayout) view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] =
                (LinearLayout) view.findViewById(R.id.row2LinearLayout);
        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }
        return view; // return the fragment's view for display
    }
    private String getSongName(String name)
    {
        return name.substring(name.indexOf(name)+1);
    }

    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button guessButton = ((Button) v);
            String guess = guessButton.getText().toString();
            String answer = getSongName(correctAnswer);
            ++totalGuesses; // increment number of guesses the user has made

            if (guess.equals(answer)) { // if the guess is correct
                ++correctAnswers; // increment the number of correct answers

                // display correct answer in green text
                answerTextView.setText(answer + "!");
                answerTextView.setTextColor(
                        getResources().getColor(R.color.correct_answer,
                                getContext().getTheme()));

                disableButtons(); // disable all guess Buttons

                // if the user has correctly identified SONGS_IN_QUIZ flags
                if (correctAnswers == SONGS_IN_QUIZ) {
                    // DialogFragment to display quiz stats and start new quiz
                    DialogFragment quizResults =
                            new DialogFragment() {
                                // create an AlertDialog and return it
                                @Override
                                public Dialog onCreateDialog(Bundle bundle) {
                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(getActivity());
                                    builder.setMessage(
                                            getString(R.string.results,
                                                    totalGuesses,
                                                    (1000 / (double) totalGuesses)));

                                    // "Reset Quiz" Button
                                    builder.setPositiveButton(R.string.reset_quiz,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int id) {
                                                    // resetQuiz();
                                                }
                                            }
                                    );

                                    return builder.create(); // return the AlertDialog
                                }
                            };

                    // use FragmentManager to display the DialogFragment
                    quizResults.setCancelable(false);
                    quizResults.show(getFragmentManager(), "quiz results");
                }
                else { // answer is correct but quiz is not over
                    // load the next flag after a 2-second delay
                    handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    //animate(true); // animate the flag off the screen
                                }
                            }, 2000); // 2000 milliseconds for 2-second delay
                }
            }
            else { // answer was incorrect
                //songImageView.startAnimation(shakeAnimation); // play shake

                // display "Incorrect!" in red
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(
                        R.color.incorrect_answer, getContext().getTheme()));
                guessButton.setEnabled(false); // disable incorrect answer
            }
        }
    };

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private void disableButtons() {
        for (int row = 0; row < guessRows; row++) {
            LinearLayout guessRow = guessLinearLayouts[row];
            for (int i = 0; i < guessRow.getChildCount(); i++)
                guessRow.getChildAt(i).setEnabled(false);
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
