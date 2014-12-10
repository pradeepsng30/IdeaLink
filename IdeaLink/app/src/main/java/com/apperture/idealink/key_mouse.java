package com.apperture.idealink;

import android.app.Activity;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link key_mouse.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link key_mouse#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class key_mouse extends Fragment implements View.OnTouchListener, View.OnKeyListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment key_mouse.
     */
    // TODO: Rename and change types and number of parameters
    public static key_mouse newInstance() {
        key_mouse fragment = new key_mouse();
       // Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
       // fragment.setArguments(args);
        return fragment;
    }
    public key_mouse() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        start();
    }

    public void start(){

        Log.d("OnCreate","Key and Mouse");
        // Display display = Activity.getWindowManager().getDefaultDisplay();
        Display display =

        int width = display.getWidth();
        View view;
        view =view.findViewById(R.id.LeftClickButton);
        Button left = (Button)view.findViewById(R.id.LeftClickButton);
        // (Button)  findViewById(R.id.LeftClickButton);
        Button right =  (Button) view.findViewById(R.id.RightClickButton);

       // left.setWidth(width/2);
        //right.setWidth(width/2);

        View touchView = (View) view.findViewById(R.id.TouchPad);
        touchView.setOnTouchListener(this);

        EditText editText = (EditText) findViewById(R.id.KeyBoard);
        editText.setOnKeyListener(this);

        editText.addTextChangedListener(new TextWatcher(){
            public void  afterTextChanged (Editable s){
                Log.d("seachScreen", ""+s);
                s.clear();
            }
            public void  beforeTextChanged  (CharSequence s, int start, int count, int after){
                Log.d("seachScreen", "beforeTextChanged");
            }
            public void  onTextChanged  (CharSequence s, int start, int before, int count) {


                try{
                    char c = s.charAt(start);
                    Backend.mCommandService.write("KEY" + "!!" + c);
                }
                catch(Exception e){}
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_key_mouse, container, false);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Called when a hardware key is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     * <p>Key presses in software keyboards will generally NOT trigger this method,
     * although some may elect to do so in some situations. Do not assume a
     * software input method has to be key-based; even if it is, it may use key presses
     * in a different way than you expect, so there is no way to reliably catch soft
     * input key presses.
     *
     * @param v       The view the key has been dispatched to.
     * @param keyCode The code for the physical key that was pressed
     * @param event   The KeyEvent object containing full information about
     *                the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
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
        public void onFragmentInteraction(Uri uri);
    }

      public void ButtonClicked(View view){
          Log.d("Key_mouse ","Button clicked");
      }
}
