package sort;

import java.lang.reflect.Array;
import java.util.Random;

public class Main {

    private static final int MAX = 10_000_000;
    private static final int N = 100_000;
   // private static final int N = 11;
    public static void main(String[] args) {
        int[] array = generate(N);
//        for (int i = 0; i < array.length; i++) {
//            System.out.println(array[i]);
//        }
        int k=10;
        int [][] parts=new int[k-1][N/k];
        int [] tail = new int[N-N/k*(k-1)];//так как разное количество элементов(тут в последнем может получиться размер больший, чем в остальных)
        long start = System.currentTimeMillis();
        for (int i = 0; i < k-1; i++) {
            System.arraycopy(array,N/k*i,parts[i],0,parts[i].length);
        }
        System.arraycopy(array,N/k*(k-1),tail,0,tail.length);
        //System.out.println(array[0]);
        //System.out.println(parts[0][0]);
        //System.out.println(parts[k-2][0]+"last part");
        Sorter[] arraySorter=new Sorter[k];
        for (int i = 0; i < k; i++) {
            if (i<k-1 ){
            arraySorter[i]=new Sorter(parts[i]);
            }
            else{//отдельно создаем хвостик
                arraySorter[i]=new Sorter(tail);
            }
            arraySorter[i].start();}
            try {
                for (int i = 0; i < k; i++) {
                arraySorter[i].join();}
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        //arraySorter[k-1]=new Sorter(tail);

       // arraySorter[k-1].start();
        //try {
            //arraySorter[k-1].join();
        //} catch (InterruptedException e) {
            //e.printStackTrace();
        //}
        //System.out.println(parts[0][0]);
        int []res=parts[0];
        for (int i = 1; i < k-1; i++) {
            res=merge(res,parts[i]);

        }
      //  for (int i = 0; i < tail.length ; i++) {
       //     System.out.println(tail[i]+"хвост");
       // }
        res=merge(res,tail);
//        for (int j = 0; j < res.length; j++) {
//            System.out.println(res[j]);
//        }

        /*
        int[] left = new int[N/2];
        int[] right = new int[N - N/2];
        for (int i = 0; i < array.length; i++) {
            if (i < array.length / 2) {
                left[i] = array[i];
            } else {
                right[i - array.length / 2] = array[i];
            }
        }

        Sorter sorterLeft = new Sorter(left);
        Sorter sorterRight = new Sorter(right);

        sorterLeft.start();
        sorterRight.start();

        // сюрприз
        try {
            sorterLeft.join();
            sorterRight.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/


        //int[] array2 = merge(left, right);
        long finish = System.currentTimeMillis();
        System.out.println(isSorted(res));
        System.out.println(finish - start + " ms");

        /**************************** подсчет оптимального количества потоков ****************************************/

        long dif_now=0;
        long dif_prev= Long.MAX_VALUE;//очень большое число
        k=2;
        int kEnd=0;
        //int kLast=2;
        int count=23;
        while(k<N+1&&count!=0){
              parts=new int[k-1][N/k];
              tail = new int[N-N/k*(k-1)];//так как разное количество элементов(тут может получится размер больший, чем в остальных)
             start = System.currentTimeMillis();
            for (int i = 0; i < k-1; i++) {
                System.arraycopy(array,N/k*i,parts[i],0,parts[i].length);
            }
            System.arraycopy(array,N/k*(k-1),tail,0,tail.length);

            arraySorter=new Sorter[k];
            for (int i = 0; i < k; i++) {
                if (i<k-1 ){
                    arraySorter[i]=new Sorter(parts[i]);
                }
                else{//отдельно создаем хвостик
                    arraySorter[i]=new Sorter(tail);
                }
                arraySorter[i].start();}
            try {
                for (int i = 0; i < k; i++) {
                    arraySorter[i].join();}
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            arraySorter=new Sorter[k];
//            for (int i = 0; i < k-1; i++) {
//                arraySorter[i]=new Sorter(parts[i]);
//                arraySorter[i].start();
//                try {
//                    arraySorter[i].join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            arraySorter[k-1]=new Sorter(tail);
            res=parts[0];
//            arraySorter[k-1].start();
//            try {
//                arraySorter[k-1].join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            for (int i = 1; i < k-1; i++) {
                res=merge(res,parts[i]);
            }

            res=merge(res,tail);
            finish = System.currentTimeMillis();
            dif_now=finish - start;
            System.out.println(k+" разница: "+dif_now);
            if(dif_prev>dif_now){//разница поменялась -> берем меньшую, новое к и запускаем счётчик заново
                dif_prev=dif_now;
                kEnd=k;
                System.out.println(kEnd+" разница изменилась "+dif_prev);//можно закомментить(использовалось для проверки)
                count=23;
            }else{count--;}//count=0(условие выхода из цикла)-> 23 раз подряд разница не изменялась в лучшую сторону
            k++;
        }
        System.out.println(isSorted(res));
        System.out.println(dif_prev+ " ms, поделить на "+ kEnd+" потоков");



       // long start1 = System.currentTimeMillis();
        //int[] sortedArray = bubbleSort(array);
        //long finish1 = System.currentTimeMillis();

        //System.out.println(isSorted(sortedArray));
        //System.out.println(finish1 - start1 + " ms");
    }

    public static int[] generate(int n) {
        Random r = new Random();
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = r.nextInt(MAX);
        }
        return array;
    }

    public static int[] bubbleSort(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = i; j < array.length; j++) {
                if (array[i] > array[j]) {
                    int t = array[i];
                    array[i] = array[j];
                    array[j] = t;
                }
            }
        }
        return array;
    }

    public static boolean isSorted(int[] array) {
        boolean sorted = true;
        for (int i = 0; i < array.length - 1; i++) {
            if(array[i] > array[i+1]){
                sorted = false;
            }
        }
        return sorted;
    }

    public static int[] merge(int[] left, int[] right) {
        int leftInd = 0;
        int rightInd = 0;
        int[] newArray = new int[left.length + right.length];
        for (int i = 0; i < newArray.length; i++) {
            if (leftInd >= left.length || rightInd >= right.length) {
                break;
            }
            if (left[leftInd] < right[rightInd]) {
                newArray[i] = left[leftInd];
                leftInd++;
            } else {
                newArray[i] = right[rightInd];
                rightInd++;
            }
        }
        if (leftInd == left.length) {
            for (int i = rightInd; i < right.length; i++) {
                newArray[leftInd + i] = right[i];
            }
        } else {
            for (int i = leftInd; i < left.length; i++) {
                newArray[rightInd + i] = left[i];
            }
        }
        return newArray;
    }
}

