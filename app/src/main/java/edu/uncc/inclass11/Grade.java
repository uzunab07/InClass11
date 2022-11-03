package edu.uncc.inclass11;

import java.io.Serializable;

public class Grade implements Serializable {
    String gradeLetter, courseName, courseNum, courseGradeId;
    long creditHours;

    public Grade(String gradeLetter, String courseName, String courseNum, long creditHours) {
        this.gradeLetter = gradeLetter;
        this.courseName = courseName;
        this.courseNum = courseNum;
        this.creditHours = creditHours;
    }

    public Grade() {
    }

    public String getGradeLetter() {
        return gradeLetter;
    }

    public void setGradeLetter(String gradeLetter) {
        this.gradeLetter = gradeLetter;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(String courseNum) {
        this.courseNum = courseNum;
    }

    public long getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(long creditHours) {
        this.creditHours = creditHours;
    }

    public String getCourseGradeId() {
        return courseGradeId;
    }

    public void setCourseGradeId(String courseGradeId) {
        this.courseGradeId = courseGradeId;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "gradeLetter='" + gradeLetter + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseNum='" + courseNum + '\'' +
                ", creditHours=" + creditHours +
                '}';
    }
}
