package Model.KDTree;

/**
 * This is a class that implements the QuickSelect algorithm which is an algorithm for finding a given index element
 * without sorting the entire array
 */
public class QuickSelect {

    public static KDFriendlyShape quickselect(KDFriendlyShape[] list, int leftIndex, int rightIndex, int k, boolean sortX) {
        if (leftIndex == rightIndex) {
            return list[leftIndex];
        }

        int pivotIndex = randomPivot(leftIndex, rightIndex);
        pivotIndex = partition(list, leftIndex, rightIndex, pivotIndex, sortX);

        if (k == pivotIndex) {
            return list[k];
        } else if (k < pivotIndex) {
            return quickselect(list, leftIndex, pivotIndex - 1, k, sortX);
        } else {
            return quickselect(list, pivotIndex + 1, rightIndex, k, sortX);
        }
    }

    private static int partition(KDFriendlyShape[] list, int leftIndex, int rightIndex, int pivotIndex, boolean sortX) {
        KDFriendlyShape pivotValue = list[pivotIndex];
        swap(list, pivotIndex, rightIndex);
        int storeIndex = leftIndex;
        for (int i = leftIndex ; i < rightIndex ; i++) {
            if (list[i].getCenter(sortX) < pivotValue.getCenter(sortX)) {
                swap(list, storeIndex, i);
                storeIndex++;
            }
        }
        swap(list, rightIndex, storeIndex);
        return storeIndex;
    }

    private static int randomPivot(int leftIndex, int rightIndex) {
        return leftIndex + (int) Math.floor(Math.random() * (rightIndex - leftIndex + 1));
    }

    /**
     * This method swaps two elements from an array with each other
     * @param list is the array containing the elements
     * @param index1 is the index for the first element to swap
     * @param index2 is the index for the second element to swap
     */
    public static void swap(KDFriendlyShape[] list, int index1, int index2) {
        KDFriendlyShape temp = list[index1];
        list[index1] = list[index2];
        list[index2] = temp;
    }
}
