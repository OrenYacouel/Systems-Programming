package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import sun.awt.image.ImageWatched;

import java.util.HashMap;
import java.util.LinkedList;
import bgu.spl.mics.Future;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private LinkedList<Model> models;
    private LinkedList<String> publishedModelsNames;
    private int currentModelIndex;
    private Future<Model> resolvedFuture;
    private int publications;
    private int papersRead;

    public Student(String name, String department, Degree status, LinkedList<Model> _models) {
        this.name = name;
        this.department = department;
        this.status = status;
        this.models = _models;
        this.publishedModelsNames = new LinkedList<>();
        this.resolvedFuture = new Future<>();
        this.currentModelIndex = 0;
    }

    public Degree getStatus() {
        return status;
    }

    public LinkedList<Model> getModels() {
        return models;
    }

    public Future<Model> getResolvedFuture() {
        return resolvedFuture;
    }

    public void setResolvedFuture(Future<Model> resolvedFuture) {
        this.resolvedFuture = resolvedFuture;
    }

    public int getCurrentModelIndex() {
        return currentModelIndex;
    }

    public void updateCurrentModelIndex() {
        if (currentModelIndex < models.size())
            this.currentModelIndex++;
    }

    public Model getCurrentModel() {
        if (getCurrentModelIndex() >= getModels().size())
            return null;
        return models.get(currentModelIndex);
    }

    public void increasePublications() {
        publications++;
    }

    public void increasePapersRead() {
        papersRead++;
    }

    public void addToPublishedNames(String name) {
        publishedModelsNames.add(name);
    }

    public LinkedList<String> getPublishedNamesList() {
        return publishedModelsNames;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }
}






