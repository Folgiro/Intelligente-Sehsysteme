public class testSuper {
    private int test = 2;

    private void printTest(){
        System.out.println("superTest");
        System.out.println(this);
    }

    public void getTest(){
        printTest();
    }
}
