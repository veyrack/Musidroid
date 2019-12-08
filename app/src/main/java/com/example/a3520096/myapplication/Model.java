package com.example.a3520096.myapplication;

import java.util.*;
import l2i013.musidroid.util.InstrumentName;

public class Model {
    ArrayList<Position> xys;
    ArrayList<Integer> duree;
    ArrayList<Position> occuper;
    int col=155;
    int lig=140;
    public Model() {
        xys = new ArrayList<>();
        duree = new ArrayList<>();
        occuper = new ArrayList<>();
    }

    void add(int x, int y) {
        xys.add(new Position(x,y));
    }

    boolean contains(int x, int y) {
        return xys.contains(new Position(x,y));
    }

    ListIterator<Position> getAll() {
        return xys.listIterator();
    }

    void setCol(int c){
        col=c;
    }
    int getCol(){
        return col;
    }
    void setLig(int l){
        lig=l;
    }
    int getLig(){
        return lig;
    }

    ArrayList<Position> PosIteratorToArray(ListIterator<Position> pos){
        ArrayList<Position> xys=new ArrayList<>();
        while(pos.hasNext()){
            xys.add(pos.next());
        }
        return xys;
    }
    ArrayList<Integer> DureeIteratorToArray(ListIterator<Integer> pos){
        ArrayList<Integer> duree=new ArrayList<>();
        while(pos.hasNext()){
            duree.add(pos.next());
        }
        return duree;
    }

    ArrayList<Position> setAll(ArrayList<Position> pos) { return xys=pos; }
    ArrayList<Integer> setDuree(ArrayList<Integer> pos) { return duree=pos; }


    void reset() {
        xys.clear();
        duree.clear();
        occuper.clear();
    }

    void addD(int d) {
        duree.add(d);
    }
    ListIterator<Integer> getAllD() {
        return duree.listIterator();
    }


    void addO(int x, int y) {
        occuper.add(new Position(x,y));
    }
    ListIterator<Position> getAllO() {
        return occuper.listIterator();
    }

    public int Octavenum;
    int getOctave(){
        return Octavenum;
    }
    void setOctave(int o){
        Octavenum=o+1;
    }

    InstrumentName inname;
    InstrumentName getInname(){
        return inname;
    }
    void setInstru(InstrumentName n){
        inname=n;
    }

    public int Temp;
    void setTempo(int T){
        Temp=T;
    }
    int getTempo (){
        return Temp;
    }


    //Utile pour le slide
    public int relatif;
    void setRelatif(int R) { relatif=R;}
    int getRelatif() { return relatif;}

    public int vertical;
    void setVertical(int R) { vertical=R;}
    int getVertical() { return vertical;}
}

