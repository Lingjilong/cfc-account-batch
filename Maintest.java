public class Maintest {
    public static void main(String[] args)  {
        String secMD5 = MD5.getMD5Str("Qq222222");//user的加密
        try {
            Thread.sleep(10000 * 30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(secMD5);
        System.out.println(MD5.getInstance().getMD5ofStr(secMD5));
    }
}
