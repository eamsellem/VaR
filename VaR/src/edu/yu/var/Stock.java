package edu.yu.var;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import jdistlib.disttest.NormalityTest;



public class Stock implements Company {

    double currentPrice;
    double variance;
    double sd;
    int timeHorizon;
    String name;
    List<Double> periodicChange = new ArrayList<Double>();
    List<Double> closing = new ArrayList<Double>();
    String [] prices = new String[999];
    List<Double> opening = new ArrayList<Double>();


    public Stock (String fileName){
        readFile(fileName);
        //this.closing = closing;
        //this.opening = opening;
        //this.variance = variance;
        //this.sd = sd;
        this.currentPrice = this.closing.get(closing.size()-1);
        this.timeHorizon = timeHorizon-1;
        this.name = fileName.substring(0, fileName.length()-4);
    }




    private void readFile(String fileName){
        File file = new File(fileName);
        try {
            Scanner inputStream = new Scanner(file);
            while(inputStream.hasNext()){
                String data = inputStream.next();
                prices = (data.split(","));
                if(prices[4].equals("Close"))
                    continue;
               // System.out.println(prices[4] + "*");
                opening.add(Double.parseDouble(prices[1]));
                closing.add(Double.parseDouble(prices[4]));
                timeHorizon++;
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

    public double getStandardDeviation(Stock stock) {
        double X = getAverageStockReturn(stock);
        double [] change = getPeriodicChange(stock);
        double x = 0;
        for(int i = 0; i < change.length; i++){
            x += Math.pow(((change[i])-X),2);
        }
        double sd = x/(change.length);
        return Math.sqrt(sd);
    }


    public boolean normallyDistributed(Stock stock){
        double test  = NormalityTest.shapiro_wilk_statistic(stock.getPeriodicChange(stock));
        return !(test >= .5);
    }

    public double getAverageStockReturn(Stock stock){
        double [] change = getPeriodicChange(stock);
        double sum = 0;
        for(int i = 0; i< change.length; i++){
            sum += change[i];
        }
        return sum/change.length;
    }

    public double getVariance(Stock stock){
        return Math.pow(stock.getStandardDeviation(stock), 2);
    }

    public List<Double> getPrices(Stock stock){
    return stock.closing;
    }

    public double getAveragePrice(Stock stock){
        double sum = 0;
        int counter = 0;
        for(int i = 0; i < stock.closing.size(); i++){
            sum += stock.closing.get(i);
            counter++;
        }
        double average = sum/counter;
        return average;
    }

    public double [] getPeriodicChange(Stock stock){
        int counter = 0;
        double [] change = new double [stock.closing.size()];
        for(int i = 1; i< stock.closing.size(); i++){
            change[i] = (Math.log(stock.closing.get(i)/stock.opening.get(i)));
        }
        return change;
    }

    public double getAveragePeriodicChange(Stock stock){
        int counter = 0;
        double sum = 0;
        double difference = 0;
        for(int i = 0; i < stock.closing.size(); i++){
            difference = ((stock.closing.get(i) - stock.opening.get(i)))/stock.opening.get(i);
            sum += difference;
            counter++;
        }
        double average = sum/counter;
        return average;
    }
}

