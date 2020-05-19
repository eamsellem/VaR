package edu.yu.var;

import java.util.Random;
import java.util.*;
import java.util.Hashtable;
import java.util.Arrays;

import edu.yu.var.Portfolio;

import javax.swing.*;
import java.util.concurrent.*;
public class MonteCarloSimulation {

    int days;
    Portfolio portfolio;

    public MonteCarloSimulation(Portfolio portfolio){
    this.portfolio = portfolio;
    this.days = days;
    }


    public static void main(String [] args){
        Stock Amazon = new Stock( "AMZN (2).csv");
        Stock Tesla = new Stock("TSLA.csv");
        Stock Zoom = new Stock("ZM.csv");
        Stock [] stocks = new Stock[3];
        Double [] weights = new Double[3];
        stocks[0] = Amazon;
        stocks[1] = Tesla;
        stocks[2] = Zoom;
        weights[0] = .5;
        weights[1] = .5;
        weights[2] = .4;
        PortfolioBuilder portfolioBuilder = new PortfolioBuilder(stocks, weights);
        Portfolio portfolio = new Portfolio(portfolioBuilder.portfolio);


        System.out.printf("Amazon's current stock price is $ %.2f %n" , Amazon.currentPrice);
        System.out.printf("Amazon's average stock price is $ %.2f %n" , Amazon.getAveragePrice(Amazon));
        System.out.printf("Amazon's average daily periodic change is $%.5f %n" ,Amazon.getAveragePeriodicChange(Amazon));
        System.out.printf("Amazon's stock price standard deviation is %.5f %n" ,Amazon.getStandardDeviation(Amazon));
        System.out.printf("Amazon's stock price variance is %.5f %n" , Amazon.getVariance(Amazon));
        System.out.println("Is the Historical daily periodic change normally distributed for Amazon? " + Amazon.normallyDistributed(Amazon));
        System.out.println();

        System.out.printf("Zoom's current stock price is $ %.2f %n" , Zoom.currentPrice);
        System.out.printf("Zoom's average stock price is $ %.2f %n" , Zoom.getAveragePrice(Zoom));
        System.out.printf("Zoom's average daily periodic change is $%.5f %n" ,Zoom.getAveragePeriodicChange(Zoom));
        System.out.printf("Zoom's stock price standard deviation is %.5f %n" ,Zoom.getStandardDeviation(Zoom));
        System.out.printf("Zoom's stock price variance is %.5f %n" , Zoom.getVariance(Zoom));
        System.out.println("Is the Historical daily periodic change normally distributed for Zoom? " + Zoom.normallyDistributed(Zoom));
        System.out.println();
        System.out.printf("Tesla's current stock price is $ %.2f %n" , Tesla.currentPrice);
        System.out.printf("Tesla's average stock price is $ %.2f %n" , Tesla.getAveragePrice(Tesla));
        System.out.printf("Tesla's average daily periodic change is $%.5f %n" ,Tesla.getAveragePeriodicChange(Tesla));
        System.out.printf("Tesla's stock price standard deviation is %.5f %n" ,Tesla.getStandardDeviation(Tesla));
        System.out.printf("Tesla's stock price variance is %.5f %n" , Tesla.getVariance(Tesla));
        System.out.println("Is the Historical daily periodic change normally distributed for Tesla? " + Tesla.normallyDistributed(Tesla));
        System.out.println();
        System.out.println(portfolio.correlationCoefficient(portfolio));

        MonteCarloSimulation mcs = new MonteCarloSimulation(portfolio);
        Hashtable<Stock, Double> fin = new Hashtable<>();
        Set<Stock> finstocks = portfolio.portfolio.keySet();

        for(Stock stock : finstocks){
            fin.put(stock, VaR(MCS(portfolio, stock, 10), .99));
        }
        Set<Stock> finAnswer = fin.keySet();
        for(Stock stonk : finAnswer){
            System.out.println("The VaR For " + stonk.name + " is " + Math.log((fin.get(stonk))/stonk.currentPrice) + "    VAR " + (( stonk.currentPrice) - fin.get(stonk)));
            System.out.println();
        }

    }

    public static double sigma(double [][] covariance, double [] weights){
        double [][] temp = new double[weights.length][covariance[0].length];
        double sum = 0;
        for(int i = 0; i < weights.length; i++){
            sum = 0;
            for(int v = 0; v < covariance[i].length; v++){
                sum += weights[i]*covariance[i][v];
            }
            temp[0][i] = sum;
        }
        sum = 0;
        for(int row = 0; row< temp.length; row++){
            sum = 0;
            for(int column = 0; column < temp[0].length; column++){
                sum += temp[row][column]*weights[column];

            }
        }
        return Math.sqrt(sum);
    }

     public static double VaR(double [] MCS, double confidence){
        int index = (int)(MCS.length*(1-confidence));
        return MCS[index];
     }



     public static void quickSort(double [] arr, int low, int high){
        int start= 0;
        int end= 0;
        double k;
        double p;

        if(low < high){

        }
        int partition = partition(arr, start, end);
        if(partition-1 > start){
            quickSort(arr, start, partition-1);
        }
        if(partition+1<end){
            quickSort(arr, partition +1, end);
        }
     }

     public static int partition(double [] arr, int start, int end){
        int pivot = (int)arr[end];
        double pivott = arr[end];

        for(int i = start; i < end; i++){
            if((int)arr[i] <pivot){
                double temp = arr[start];
                arr[start] = arr[i];
                arr[i] = temp;
                start++;
            }
        }

        double temp = arr[start];
        arr[start] = pivot;
        arr[end] = temp;
        return start;
     }


    public static double [] MCS(Portfolio portfolio, Stock stock, int days){
        int runs = 1000;
        double [] epsilon = new double [runs];
        Random Rand = new Random();
        for(int i = 0; i < runs; i++) {
            epsilon[i] = portfolio.sampleCorrelatedVariables(portfolio.getCholeskyDecomposition(portfolio))[0];
        }
        Hashtable<Stock, double[]> MCS = new Hashtable<Stock, double[]>();
        double[] MonteCarlo = new double [runs];
            double s = stock.currentPrice;
            for(int j = 0; j< runs; j++) {
                for (int t = 0; t < days; t++) {
                    s = s * Math.exp(((stock.getAveragePeriodicChange(stock) - (stock.getVariance(stock)/2)) + ((stock.getStandardDeviation(stock)) * portfolio.sampleCorrelatedVariables(portfolio.getCholeskyDecomposition(portfolio))[0])));
                }
                MonteCarlo[j] = s;
                s = stock.currentPrice;
            }
            Arrays.sort(MonteCarlo, 0, MonteCarlo.length-1);

        return MonteCarlo;
    }
}

