package edu.uncc.inclass11;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import edu.uncc.inclass11.databinding.FragmentGradesBinding;
import edu.uncc.inclass11.databinding.GradeRowItemBinding;

public class GradesFragment extends Fragment {

    private FirebaseAuth mAuth;

    public GradesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id==R.id.addo){
            mlisten.addGrade();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    FragmentGradesBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGradesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerViewHolder.setLayoutManager(new LinearLayoutManager(getContext()));
        gradeAdapter = new GradeAdapter();
        binding.recyclerViewHolder.setAdapter(gradeAdapter);

        getActivity().setTitle("Grades");

        getGrades();
    }

    GradeAdapter gradeAdapter;
    ArrayList<Grade> grades = new ArrayList<>();

    class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradesViewHolder> {
        @NonNull
        @Override
        public GradesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            GradeRowItemBinding binding = GradeRowItemBinding.inflate(getLayoutInflater(), parent, false);
            return new GradesViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull GradesViewHolder holder, int position) {
            Grade grade = grades.get(position);
            holder.setupUi(grade);
        }

        @Override
        public int getItemCount() {
            return grades.size();
        }

        class GradesViewHolder extends RecyclerView.ViewHolder {

            GradeRowItemBinding mBinding;
            Grade mGrade;
            public GradesViewHolder(@NonNull GradeRowItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUi(Grade grade){
                mGrade = grade;


                mBinding.textViewCourseHours.setText(String.valueOf(mGrade.getCreditHours()) + " credit hours");

                mBinding.textViewCourseLetterGrade.setText(mGrade.getGradeLetter());

                mBinding.textViewCourseName.setText(mGrade.getCourseName());

                mBinding.textViewCourseNumber.setText(mGrade.getCourseNum());

                mBinding.imageViewDelete.setImageResource(R.drawable.rubbish_bin);

                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("grades").document(mGrade.getCourseGradeId())
                                .delete().addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("demo", "onSuccess: Successfully deleted");
                                    }
                                });
                    }
                });
            }
        }


    }

    public void getGrades(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        db.collection("grades").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!= null ){
                    Log.d("demo", "onEventError: "+error);
                } else {
                    grades.clear();
                    int totalHours = 0;
                    double totalGradePoints = 0;
                    double GPA = 0;
                    for (QueryDocumentSnapshot document : value) {
                        Grade grade = new Grade(document.getString("gradeLetter"),
                                document.getString("courseName"),
                                document.getString("courseNum"),
                                document.getLong("creditHours"));
                        grade.setCourseGradeId(document.getId());

                        if (document.getString("grade_uid").equals(mAuth.getUid())) {
                            int gradePoint = 0;
                            totalHours += (int) grade.getCreditHours();

                            switch (grade.getGradeLetter()) {
                                case "A":
                                    gradePoint = 4;
                                    break;
                                case "B":
                                    gradePoint = 3;
                                    break;
                                case "C":
                                    gradePoint = 2;
                                    break;
                                case "D":
                                    gradePoint = 1;
                                    break;
                                case "F":
                                    gradePoint = 0;
                                    break;
                            }

                            totalGradePoints += gradePoint * (int) grade.getCreditHours();
                            Log.d("demo", "onGradeAdded :" + totalGradePoints);
                            grades.add(grade);

                        }

                        GPA = totalGradePoints / totalHours;

                        if(totalHours == 0) {
                            binding.textViewGPA.setText("GPA: 4");
                        } else {
                            binding.textViewGPA.setText("GPA: " + String.format("%.1f", GPA));
                        }
                        binding.textView2.setText("Hours: " + totalHours);


                        Log.d("demo", "onComplete: " + document.getId());
                        Log.d("demo", "onComplete: " + document.getData());
                    }
                    gradeAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    gradeListener mlisten;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mlisten = (gradeListener) context;
    }

    public interface gradeListener {
        void addGrade();
    }
}