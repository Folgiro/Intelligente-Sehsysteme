public class testSub extends testSuper{
    public int test = 1;


    public void printTest(){
        System.out.println("subTest");
        System.out.println(this);
    }

    public void getTest(){
        printTest();
    }
}
