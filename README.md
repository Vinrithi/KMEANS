# KMEANS
The program implements Kmeans clustering algorithm. It uses the dataset provided in the code to cluster it according to how closely related they are. 
The algorithm basically goes as follows:
1.  Given a whole list of dataset, determine the number of clusters K you will need. 
2.  Randomly pick a datasets to be the centroids of these clusters i.e. if  k= 3, then randomly pick 3 centroids from the whole list.
3.  For each dataset, determine the euclidean distance to all the centroids. Then use the minimum distance and place the dataset to be in     the same group as that centroid. 
4.  Once the clusters have been allocated members by using minimum distances, determine new centroids for these clusters by using the         average of the datasets of the members of the group.
5.  Repeat steps 6 and 7 until there is no change in clusters by members, i.e. members no longer shift groups. This means stability has       been achieved.

