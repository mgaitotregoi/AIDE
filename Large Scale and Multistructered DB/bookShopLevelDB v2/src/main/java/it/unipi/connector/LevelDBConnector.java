package it.unipi.connector;

import org.iq80.leveldb.*;
import java.io.*;
import java.util.*;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class LevelDBConnector {

    private DB db = null;

    private static class MyDBComparator implements DBComparator {
        @Override
        public String name() {
            return "simple";
        }
        @Override
        public byte[] findShortestSeparator(byte[] start, byte[] limit) {
            return start;
        }
        @Override
        public byte[] findShortSuccessor(byte[] key) {
            return key; }
        @Override
        public int compare(byte[] o1, byte[] o2) {
        String k1 = asString(o1);
        String k2 = asString(o2);
        String[] parts1 = k1.split(":");
        String[] parts2 = k2.split(":");
        int comparison = 0;
        for(int i = 0; i < parts1.length && i < parts2.length;i++){
            if(i!=1){
                comparison = parts1[i].compareTo(parts2[i]);
            }else {
                comparison = (Integer.valueOf(parts1[i])).compareTo(Integer.valueOf(parts2[i]));
            }
            if(comparison != 0) break;
        }
            return comparison;
        }
    }

     public void openDB(String file){
        Options options = new Options();
        //options.comparator(new MyDBComparator());
        options.createIfMissing(true);

        try{
            db = factory.open(new File(file), options);
        }
        catch (IOException ioe) { closeDB(); }
    }

    public void put(String key, String value)
    {
        db.put(bytes(key), bytes(value));
    }
    public void put(String key, int value)
    {
        db.put(bytes(key), bytes(String.valueOf(value)));
    }

    public String get(String key){
        byte[] bytes = db.get(bytes(key));
        return (bytes == null ? null : asString(bytes));
    }

    public void writeBatch(HashMap<String,String> kv){
        try(WriteBatch batch = db.createWriteBatch()) {
            for (Map.Entry<String, String> entry : kv.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                batch.put(bytes(key),bytes(value));
            }
            db.write(batch);
        }catch (IOException ioe) {ioe.printStackTrace(); }
    }

    public void deleteBatch(List<String> dList){
        try(WriteBatch batch = db.createWriteBatch()) {
            for (String delval: dList) {
                batch.delete(bytes(delval));
            }
            db.write(batch);
        }catch (IOException ioe) {ioe.printStackTrace(); }
    }

    public DBIterator getIterator(){
        return db.iterator();
    }

    public void deleteValue(String key)
    {
        db.delete(bytes(key));
    }

    public void closeDB(){
        try {
            if( db != null) db.close();
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }

    public void Iterator(String file) {
        System.out.println("-------------------------");
        openDB(file);
        try (DBIterator iterator = db.iterator()) {
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                String value = asString(iterator.peekNext().getValue());
                System.out.println(key + " = " + value);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            closeDB();
            System.out.println("-------------------------");
        }
    }

}

