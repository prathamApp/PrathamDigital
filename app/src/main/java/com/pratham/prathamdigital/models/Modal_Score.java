package com.pratham.prathamdigital.models;

/**
 * Created by HP on 16-08-2017.
 */

public class Modal_Score {
    public String SessionId;
    public String ResourceId;
    public int QuestionId;
    public int ScoredMarks;
    public int TotalMarks;
    public int Level;
    public String StartTime;
    public String EndTime;
    public String DeviceId;
    public String Location;
    public int sentFlag; //0 = Not Sent, 1 = Sent


    @Override
    public String toString() {
        return "Modal_Score{" +
                "SessionId='" + SessionId + '\'' +
                ", ResourceId='" + ResourceId + '\'' +
                ", QuestionId=" + QuestionId +
                ", ScoredMarks=" + ScoredMarks +
                ", TotalMarks=" + TotalMarks +
                ", Level=" + Level +
                ", StartTime='" + StartTime + '\'' +
                ", EndTime='" + EndTime + '\'' +
                ", DeviceId='" + DeviceId + '\'' +
                ", Location='" + Location + '\'' +
                ", sentFlag=" + sentFlag +
                '}';
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String sessionId) {
        SessionId = sessionId;
    }

    public String getResourceId() {
        return ResourceId;
    }

    public void setResourceId(String resourceId) {
        ResourceId = resourceId;
    }

    public int getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(int questionId) {
        QuestionId = questionId;
    }

    public int getScoredMarks() {
        return ScoredMarks;
    }

    public void setScoredMarks(int scoredMarks) {
        ScoredMarks = scoredMarks;
    }

    public int getTotalMarks() {
        return TotalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        TotalMarks = totalMarks;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        Level = level;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }
}