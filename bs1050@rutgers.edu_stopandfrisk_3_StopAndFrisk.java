import java.util.ArrayList;

/**
 * The StopAndFrisk class represents stop-and-frisk data, provided by
 * the New York Police Department (NYPD), that is used to compare
 * during when the policy was put in place and after the policy ended.
 * 
 * @author Tanvi Yamarthy
 * @author Vidushi Jindal
 */
public class StopAndFrisk {

    /*
     * The ArrayList keeps track of years that are loaded from CSV data file.
     * Each SFYear corresponds to 1 year of SFRecords. 
     * Each SFRecord corresponds to one stop and frisk occurrence.
     */ 
    private ArrayList<SFYear> database; 

    /*
     * Constructor creates and initializes the @database array
     * 
     * DO NOT update nor remove this constructor
     */
    public StopAndFrisk () {
        database = new ArrayList<>();
    }

    /*
     * Getter method for the database.
     * *** DO NOT REMOVE nor update this method ****
     */
    public ArrayList<SFYear> getDatabase() {
        return database;
    }

    /**
     * This method reads the records information from an input csv file and populates 
     * the database.
     * 
     * Each stop and frisk record is a line in the input csv file.
     * 
     * 1. Open file utilizing StdIn.setFile(csvFile)
     * 2. While the input still contains lines:
     *    - Read a record line (see assignment description on how to do this)
     *    - Create an object of type SFRecord containing the record information
     *    - If the record's year has already is present in the database:
     *        - Add the SFRecord to the year's records
     *    - If the record's year is not present in the database:
     *        - Create a new SFYear 
     *        - Add the SFRecord to the new SFYear
     *        - Add the new SFYear to the database ArrayList
     * 
     * @param csvFile
     */
    public void readFile ( String csvFile ) {

        // DO NOT remove these two lines
        StdIn.setFile(csvFile); // Opens the file
        StdIn.readLine();       // Reads and discards the header line


        while (!StdIn.isEmpty()) {
            String[] recordEntries = StdIn.readLine().split(",");

            int year = Integer.parseInt(recordEntries[0]);
            String description = recordEntries[2];
            String gender = recordEntries[52];
            String location = recordEntries[71];
            String race = recordEntries[66];
            boolean frisked = recordEntries[16].equals("Y");
            boolean arrested = recordEntries[13].equals("Y");

            SFRecord record = new SFRecord(description, arrested, frisked, gender, race, location);

            SFYear sfYear = findYear(year);
            if (sfYear == null) {
                sfYear = new SFYear(year);
                database.add(sfYear);
            }

            sfYear.addRecord(record);
        }
    }

    private SFYear findYear(int year) {
        for (SFYear sfYear : database) {
            if (sfYear.getcurrentYear() == year) {
                return sfYear;
            }
        }
        return null;
    }
    
    /**
     * This method returns the stop and frisk records of a given year where 
     * the people that was stopped was of the specified race.
     * 
     * @param year we are only interested in the records of year.
     * @param race we are only interested in the records of stops of people of race. 
     * @return an ArrayList containing all stop and frisk records for people of the 
     * parameters race and year.
     */

    
     public ArrayList<SFRecord> populationStopped ( int year, String race ) {

         ArrayList<SFRecord> records = new ArrayList<>();
        SFYear sfYear = findYear(year);
        if (sfYear != null) {
            for (SFRecord record : sfYear.getRecordsForYear()) {
                if (record.getRace().equalsIgnoreCase(race)) {
                    records.add(record);
                }
            }
        }
        return records;
    }


    /**
     * This method computes the percentage of records where the person was frisked and the
     * percentage of records where the person was arrested.
     * 
     * @param year we are only interested in the records of year.
     * @return the percent of the population that were frisked and the percent that
     *         were arrested.
     */
    public double[] friskedVSArrested ( int year ) {
        

SFYear sfYear = findYear(year);
          int total = 0;
          int frisked = 0; 
          int arrested = 0;
         if (sfYear != null) {
             for (SFRecord record : sfYear.getRecordsForYear()) {
            total++;
                 if (record.getFrisked()) {
                frisked++;
            }
               if (record.getArrested()) {
                   arrested++;
            }
        }
    }
    return new double[]{(double) frisked / total * 100, (double) arrested / total * 100};
}
    

    /**
     * This method keeps track of the fraction of Black females, Black males,
     * White females and White males that were stopped for any reason.
     * Drawing out the exact table helps visualize the gender bias.
     * 
     * @param year we are only interested in the records of year.
     * @return a 2D array of percent of number of White and Black females
     *         versus the number of White and Black males.
     */
    public double[][] genderBias ( int year ) {

  SFYear sfYear = findYear(year);
        if (sfYear == null) {
                return new double[2][3]; 
            }
        
       int blackFemales = 0, blackMales = 0, whiteFemales = 0, whiteMales = 0;
        
            for (SFRecord record : sfYear.getRecordsForYear()) {
         if ("B".equalsIgnoreCase(record.getRace())) {
                    if ("F".equalsIgnoreCase(record.getGender())) {
                  blackFemales++;
                    } else if ("M".equalsIgnoreCase(record.getGender())) {
                        blackMales++;
                    }
                } else if ("W".equalsIgnoreCase(record.getRace())) {
                    if ("F".equalsIgnoreCase(record.getGender())) {
                        whiteFemales++;
                    } else if ("M".equalsIgnoreCase(record.getGender())) {
                        whiteMales++;
                }
                }
            }
        
            double totalBlack = blackFemales + blackMales;

            double totalWhite = whiteFemales + whiteMales;

            double totalPopulation = totalBlack + totalWhite;
      
            
            double[][] bias = new double[2][3];
       bias[0][0] = totalBlack > 0 ? (double) blackFemales / totalBlack * 100.0 : 0;
       
            bias[0][1] = totalWhite > 0 ? (double) whiteFemales / totalWhite * 100.0 : 0;
    
            bias[0][2] = totalPopulation > 0 ? (double) (blackFemales + whiteFemales) / totalPopulation * 100.0 : 0;
    
            
            
   bias[1][0] = totalBlack > 0 ? (double) blackMales / totalBlack * 100.0 : 0;
         
       bias[1][1] = totalWhite > 0 ? (double) whiteMales / totalWhite * 100.0 : 0;
          
      bias[1][2] = totalPopulation > 0 ? (double) (blackMales + whiteMales) / totalPopulation * 100.0 : 0;
        
            return bias;
        }
    /**
     * This method checks to see if there has been increase or decrease 
     * in a certain crime from year 1 to year 2.
     * 
     * Expect year1 to preceed year2 or be equal.
     * 
     * @param crimeDescription
     * @param year1 first year to compare.
     * @param year2 second year to compare.
     * @return 
     */


    public double crimeIncrease ( String crimeDescription, int year1, int year2 ) {
        
int countYear1 = 0, countYear2 = 0;
    int totalYear1 = 0, totalYear2 = 0;


    for (SFYear sfYear : database) {
  
        if (sfYear.getcurrentYear() == year1) {
            for (SFRecord record : sfYear.getRecordsForYear()) {
                totalYear1++;
                if (record.getDescription().indexOf(crimeDescription) >= 0) {
                    countYear1++;
                }
            }
        } else if (sfYear.getcurrentYear() == year2) {
     
            for (SFRecord record : sfYear.getRecordsForYear()) {
                totalYear2++;
                if (record.getDescription().indexOf(crimeDescription) >= 0) {
                    countYear2++;
                }
            }
        }
    }

    double percentageYear1 = totalYear1 > 0 ? (double) countYear1 / totalYear1 * 100 : 0;
    double percentageYear2 = totalYear2 > 0 ? (double) countYear2 / totalYear2 * 100 : 0;

    return percentageYear2 - percentageYear1; 
}
    


    /**
     * This method outputs the NYC borough where the most amount of stops 
     * occurred in a given year. This method will mainly analyze the five 
     * following boroughs in New York City: Brooklyn, Manhattan, Bronx, 
     * Queens, and Staten Island.
     *
     * @param year we are only interested in the records of year.
     * @return the borough with the greatest number of 
     */
    public String mostCommonBorough ( int year ) {

       
     SFYear sfYear = findYear(year);
    if (sfYear == null) {
        return null;
    }

   
          String[] boroughs = {"BROOKLYN", "MANHATTAN", "BRONX", "QUEENS", "STATEN ISLAND"};
          String[] formattedBoroughs = {"Brooklyn", "Manhattan", "Bronx", "Queens", "Staten Island"};
   
   
    int[] boroughCounts = new int[boroughs.length];

    for (SFRecord record : sfYear.getRecordsForYear()) {
 
        String location = record.getLocation().toUpperCase();
        for (int i = 0; i < boroughs.length; i++) {
            if (location.equals(boroughs[i])) {
                boroughCounts[i]++;
    
                   break;
            }
        }
    
     }

    int maxIndex = 0;
   
          for (int i = 1; i < boroughCounts.length; i++) {
             if (boroughCounts[i] > boroughCounts[maxIndex]) {
    

             maxIndex = i;
        }
    }

    return formattedBoroughs[maxIndex];
}
}