package vinrithi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        List<Medicine> list = new ArrayList<>();

        //List containing the unclustered medicine and their values.
        list.add(new Medicine('A', 1, 1));
        list.add(new Medicine('B', 2, 1));
        list.add(new Medicine('C', 4, 3));
        list.add(new Medicine('D', 5, 4));
        list.add(new Medicine('E', 3, 6));
        list.add(new Medicine('F', 2, 2));
        list.add(new Medicine('G', 3, 1));
        list.add(new Medicine('H', 5, 6));

        int listSize = list.size();
        int medCoordinates[][] = new int[2][listSize]; //array which stores the medicine values in a matrix form

        //loop to store medicine values in the medicine array
        for (int j = 0; j < listSize; j++) {
            medCoordinates[0][j] = list.get(j).getWeight();
            medCoordinates[1][j] = list.get(j).getPhvalue();
        }

        int noOfClusters = determineGroups(listSize); //function which produces the number of clusters to use

        //first centroids, which are randomly selected from the whole list
        double centroidsCoordinates[][] = getCentroids(list, listSize, noOfClusters);
        double prevMatrix[][] = new double[2][noOfClusters]; //matrix which stores the previous distance matrix for comparison
        int iterationNumber = 1; //counts the number of iterations undertaken

        //infinite loop for iterating until stability. It only stops when the previous and current distance matrices are same.
        for(;;)
        {
            System.out.println("\n\n******Iteration "+iterationNumber+" ***********");
            //list to hold all groups of medicine after each clustering epoch
            List<List<Medicine>> groups = new ArrayList<>();

            //displaying the centroids being used in current epoch
            System.out.println("\t\t-------Centroids----------");
            for(int i=0;i<noOfClusters;i++)
            {
                System.out.println("\t\t\tC"+i+"("+centroidsCoordinates[0][i]+", "+centroidsCoordinates[1][i] + ")");
            }

            //determine and display the distance matrix of the medicine coordinates for each group
            System.out.println("\n\t\t-------Distance matrix-----------");
            double d0[][] = groupMedicine(medCoordinates, centroidsCoordinates, noOfClusters, listSize);
            for (int i = 0; i < noOfClusters; i++) {
                List<Medicine> list1 = new ArrayList<>(); //creates a new group
                for (int j = 0; j < listSize; j++) {
                    if (d0[i][j] == 1.0) { //checks for minimum distance and adds it in the group if value is 1 in the current distance matrix.
                        list1.add(list.get(j));
                    }
                }
                groups.add(list1); //add the group to the list of groups
            }

            //for each group determine the new centroids to be used for next epoch
            for (int i = 0; i < noOfClusters; i++) {
                getPerGroupCentroids(groups.get(i),centroidsCoordinates,i);
            }

            //check if previous and current distane matrices are equal and break if true; meaning stability has been achieved
            if(Arrays.deepEquals(prevMatrix, d0))
            {
                System.out.println("\nStability was achieved");
                break;
            }
            prevMatrix = d0.clone(); //make previous distance matrix to have values of the current distance matrix
            iterationNumber++;
        }
    }

    //function used to get the centroids from the initial list by using randomization
    private static double[][] getCentroids(List<Medicine> list, int listSize, int noOfClusters) {
        double centroidsCoordinates[][] = new double[2][noOfClusters];
        List<Integer> chosenIndices = new ArrayList<>();
        for (int i = 0; i < noOfClusters; i++) {
            int index = getCentroidLocation(0, listSize, chosenIndices);
            chosenIndices.add(index);
            centroidsCoordinates[0][i] = list.get(index).getWeight();
            centroidsCoordinates[1][i] = list.get(index).getPhvalue();
        }
        return centroidsCoordinates; //return an array containing the centroids
    }

    //function for getting centroids for each group by finding averages of the weight and ph values
    private static void getPerGroupCentroids(List<Medicine> list,double centroidsCoordinates[][] , int arrposition) {
        System.out.println("\n\t\t------For Group "+arrposition+" --------");
        int listSize = list.size();
        double sumWeight = 0,sumPh = 0;
        for (Medicine medicine : list) {
            sumWeight += medicine.getWeight();
            sumPh += medicine.getPhvalue();
            System.out.println("\t\t\tMedice = "+medicine.getMedicine()+" Weight = "+medicine.getWeight()+ " PH = "+medicine.getPhvalue());
        }
        System.out.println("\t\t\tThe centroid for the group is ("+sumWeight/listSize+", "+sumPh/listSize + ")");
        centroidsCoordinates[0][arrposition] = sumWeight/listSize;
        centroidsCoordinates[1][arrposition] = sumPh/listSize;
        //no need for return since arrays are passed by reference
    }


    //takes the list size and uses square root and division to return number of clusters to have
    private static int determineGroups(int listSize) {
        int noOfClusters = (int) ((double) listSize / Math.sqrt((double) listSize));
        return (noOfClusters * noOfClusters < listSize) ? noOfClusters + 1 : noOfClusters;
    }

    //returns the position of the centroid chosen in order to get its coordinates from the main list
    private static int getCentroidLocation(int start, int end, List<Integer> chosenIndices) {
        int val = ThreadLocalRandom.current().nextInt(start, end);
        if (chosenIndices!= null && chosenIndices.contains(val)) {
            val = getCentroidLocation(start, end, chosenIndices); //recurse until unused index is picked
        }
        return val;
    }

    //main function which uses the medicine coordinates to produce their matrix of distance from the centroids
    private static double[][] groupMedicine(int medicine[][], double centroids[][], int noOfClusters, int medLen) {
        double newMatrixStructure[][] = new double[noOfClusters][medLen];

        //Calculates the Euclidean distance of the medicine coordinates from the centroids and places the distances in a matrix
        for (int i = 0; i < medLen; i++) {
            for (int j = 0; j < noOfClusters; j++) {
                double part1 = Math.pow((medicine[0][i] - centroids[0][j]), 2);
                double part2 = Math.pow((medicine[1][i] - centroids[1][j]), 2);
                double val = Math.sqrt((part1 + part2));
                newMatrixStructure[j][i] = val;
            }
        }

        //for each matrix column, the smallest distance is found and the distance matrix is replaced at that position with a 1.
        //The rest are made 0s.
        for (int i = 0; i < medLen; i++) {
            double min = newMatrixStructure[0][i];
            int index = 0;
            for (int j = 0; j < noOfClusters; j++) {
                if (newMatrixStructure[j][i] < min) {
                    min = newMatrixStructure[j][i];
                    index = j;
                }
            }

            for (int j = 0; j < noOfClusters; j++) {
                if (j != index) newMatrixStructure[j][i] = 0;
                else newMatrixStructure[j][i] = 1;
            }
        }

        //displays the distance matrix
        for (int i = 0; i < noOfClusters; i++) {
            for (int j = 0; j < medLen; j++) {
                System.out.print("\t"+newMatrixStructure[i][j] + "  ");
            }
            System.out.println("");
        }
        return newMatrixStructure;
    }
}

//class whose instance holds the name of medicine, weight and ph value as one record.
class Medicine {
    private char medicine;
    private int weight, phvalue;

    public Medicine(char medicine, int weight, int phvalue) {
        this.medicine = medicine;
        this.weight = weight;
        this.phvalue = phvalue;
    }

    public char getMedicine() {
        return medicine;
    }

    public int getWeight() {
        return weight;
    }

    public int getPhvalue() {
        return phvalue;
    }
}
