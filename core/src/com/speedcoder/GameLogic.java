package com.speedcoder;

import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Created by ilker on 15.08.2018.
 */

public class GameLogic {
    private Random random;
    private String[] codes;
    private int nextDelay;

    private float millisPerChar = 444f;
    private float difficultyMultiplier = .98f;

    private HashMap<Character, Integer> backspacedChars;
    public int keysTyped =0;
    public int correctLines=0,wrongLines=0;
    public long elapsedTime=0;

    public GameLogic(){
        random= new Random();
        nextDelay=5000;
        backspacedChars = new HashMap<Character, Integer>();
    }

    public GameLogic(FileHandle fileHandle){
        this();
        readCodes(fileHandle.readString());
    }

    public void readCodes(String s){
        codes = s.split("\n");
    }

    public String getRandomCode(){
        String s=codes[random.nextInt(codes.length)];
        nextDelay= (int) (s.length() * millisPerChar);
        millisPerChar *= difficultyMultiplier;
        //System.out.println(millisPerChar);
        return s;
    }

    public int getDelay(){
        return nextDelay;
    }

    public void backspaced(String difference) {
        for(int i=0;i<difference.length(); ++i){
            char c=difference.charAt(i);
            if(!backspacedChars.containsKey(c))backspacedChars.put(c,1);
            else backspacedChars.put(c,backspacedChars.get(c)+1);
        }
    }

    public LinkedHashMap<Character,Integer> sortBackspacedChars(){
        return Utils.sortByValues(backspacedChars);
    }

    public int getBackspaceCount(){
        int sum = 0;
        for (Object o : backspacedChars.values().toArray()) {
            sum += (Integer)o;
        }
        return sum;
    }


}
