package statistic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatisticCalculator {
	double mean;
	double stdDeviation;
	double upperBound;
	double lowerBound;
	List<Integer> tickets = new ArrayList<>();
	List<String> orderedDates = new ArrayList<>();

	public StatisticCalculator(int entries, List<String> listDate) {
		
		var logger = Logger.getLogger(StatisticCalculator.class.getName());
		var i = 0;
		for (; i < entries; i++) {
			String date = listDate.get(i);
			if (orderedDates.contains(date)) {
				int index = orderedDates.indexOf(date);
				int value = tickets.get(index) + 1;
				tickets.set(index, value);
			}
			else {
				orderedDates.add(date);
				tickets.add(1);
			}
         }  
		logger.log(Level.INFO, () ->  tickets.toString() + "\n");
		logger.log(Level.INFO, () -> orderedDates.toString() + "\n");
		
		mean = calculateMean();
		logger.log(Level.INFO, "The average is:{0}\n", mean);
        
        stdDeviation = calculateVariance();
        logger.log(Level.INFO, "The standard deviation is: {0}\n", stdDeviation);
		
		calculateBounds();
		logger.log(Level.INFO, "The lower bound is: {0}\n", lowerBound);
		logger.log(Level.INFO, "The upper bound is: {0}\n", upperBound);
		
		writeFile(logger);
	}
	
	public double calculateMean() {
		double total = 0;
		for(var i=0; i<tickets.size(); i++){
        	total = total + tickets.get(i);
        }

        return total / tickets.size();
	}
	
	public double calculateVariance() {
		double variance = 0;
		for (var i = 0; i < tickets.size(); i++) {
		    variance = variance + Math.pow(tickets.get(i) - mean, 2);
		}
		stdDeviation = Math.sqrt(variance / tickets.size());

		return stdDeviation;
	}
	
	public void calculateBounds() {
		upperBound = mean + 3*stdDeviation;
		lowerBound = mean - 3*stdDeviation;
		if (lowerBound < 0) {
			lowerBound = 0;
		}
	}
	
	public void writeFile(Logger logger) {
		var delimiter = ";";
		var user = "Gian Marco\\";
		String path = "C:\\Users\\" +  user + "Desktop\\CSVdata.csv";
		File file;
		file = new File(path);
		if (file.exists())
			logger.log(Level.INFO, "Il file {0} esiste", path);
		else
			try {
				if (file.createNewFile())
					logger.log(Level.INFO, "Il file {0} è stato creato", path);
				else
					logger.log(Level.INFO, "Il file {0} non può essere creato", path);
			} catch (IOException e) {
				e.printStackTrace();
		}
		try (
				var writer = new BufferedWriter(new FileWriter(file));
				) {
			for(var i = 0; i < orderedDates.size(); i++) {
				writer.write(orderedDates.get(i) + delimiter + tickets.get(i) + delimiter +
						String.valueOf(mean).replace('.', ',') + delimiter + 
						String.valueOf(lowerBound).replace('.', ',')+ delimiter + 
						String.valueOf(upperBound).replace('.', ',') + "\n");
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
