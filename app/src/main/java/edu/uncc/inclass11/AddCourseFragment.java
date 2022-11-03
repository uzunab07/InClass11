package edu.uncc.inclass11;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import edu.uncc.inclass11.databinding.FragmentAddCourseBinding;

public class AddCourseFragment extends Fragment {
    private FirebaseAuth mAuth;
    public AddCourseFragment() {
        // Required empty public constructor
    }



    FragmentAddCourseBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddCourseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String courseNumber = binding.editTextCourseNumber.getText().toString();
                String courseName = binding.editTextCourseName.getText().toString();
                int courseHours = Integer.parseInt(binding.editTextCourseHours.getText().toString());
                int selectedId = binding.radioGroupGrades.getCheckedRadioButtonId();

                if(courseName.isEmpty() || courseNumber.isEmpty() || binding.editTextCourseHours.getText().toString().isEmpty()) {
                   Toast.makeText(getContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else if(selectedId == -1){
                    Toast.makeText(getContext(), "Please select a letter grade !!", Toast.LENGTH_SHORT).show();
                } else {
                    String courseLetterGrade;
                    if(selectedId == R.id.radioButtonA) {
                        courseLetterGrade = "A";
                    } else if(selectedId == R.id.radioButtonB) {
                        courseLetterGrade = "B";
                    } else if(selectedId == R.id.radioButtonC) {
                        courseLetterGrade = "C";
                    } else if(selectedId == R.id.radioButtonD) {
                        courseLetterGrade = "D";
                    } else {
                        courseLetterGrade = "F";
                    }

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    mAuth = FirebaseAuth.getInstance();

                    HashMap<String, Object> course = new HashMap<>();

                    course.put("gradeLetter", courseLetterGrade);
                    course.put("courseName", courseName);
                    course.put("courseNum", courseNumber);
                    course.put("creditHours", courseHours);
                    course.put("grade_uid", mAuth.getCurrentUser().getUid());

                    db.collection("grades").add(course).addOnCompleteListener(getActivity(), new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                Log.d("demo", "onComplete: Success");
                                addCourseListener.returnBack();
                            } else {
                                Log.d("demo", "onFailure: "+task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCourseListener.goBack();
            }
        });

        getActivity().setTitle("Add Course");

    }

    AddCourseListener addCourseListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addCourseListener = (AddCourseListener) context;
    }

    interface AddCourseListener{
        void goBack();
        void returnBack();
    }
}