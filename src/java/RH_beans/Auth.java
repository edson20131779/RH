package RH_beans;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Acer
 */
public class Auth extends javax.servlet.http.HttpServlet {
           
   public void setLoginUser (String LogUser) {
       this.LoginUser = LogUser; 
   }
   
   public void setLoginPass (String LogPass) {
       this.LoginPass = LogPass;
   }
   
   public boolean getLogin(){
       // corrigido o construtor para apenas rentornar verdadiro/falso.
        this.ErrorDetected = "";
        if (CheckLogin(this.LoginUser, this.LoginPass).contains("error")) {
            String segments[] = CheckLogin(this.LoginUser, this.LoginPass).split(";");
            this.ErrorDetected = segments[segments.length - 1];
            return false;
        } else {
            return true;
        }
   }
   
   public String getError(){
       return this.ErrorDetected;
   }
   
   public String getLoginUser(){
       return this.LoginUser;
   }
   
   private String LoginUser;
   private String LoginPass;
   private String ErrorDetected;
   
   protected void processRequest(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
           throws javax.servlet.ServletException, java.io.IOException {
       response.setContentType("text/html/charset=UTF-8");
       java.io.PrintWriter out = response.getWriter();
       this.LoginUser = request.getParameter("Name");
       this.LoginPass = request.getParameter("Password");
       try {
           if (this.getLogin()){
               javax.servlet.http.HttpSession session = request.getSession();
               session.setAttribute("user", LoginUser);
               javax.servlet.RequestDispatcher rd = request.getRequestDispatcher("rh.jsp");
               rd.forward(request, response);
           }else {
               javax.servlet.RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
               rd.include(request, response);
           }
               
       } finally {
           out.close();
       }
   }

   private boolean User(String user, boolean n) {   
    //n tem que ser "true" na primeira chamada
    boolean verify = false;
    String SQLQuery = " SELECT * from account_login where Name = '"+user+"'";
    if (SQLconnector.SQLconnetionALIVE() && n) {
        if (SQLconnector.SQLVerify(SQLQuery,user,false)) {
            verify = true;
        }
    } else {
        if (n){
            Conf.Connect();
            User(user, false);
        }
        if (SQLconnector.SQLconnetionALIVE() && !n){
            if (SQLconnector.SQLVerify(SQLQuery, user, false)){
                verify = true;
            }
        }
    }
    /* se nao for = verdade retorna o false declarado acima. */
    return verify;
   }

   private boolean Pass(String password, boolean n ) {
       boolean verify = false;
       String str = Conf.encrypt(password);
       String SQLQuery = "SELECT * FROM account_login WHERE Nome=\"" + LoginUser + "\" AND Password='"+str+"';";

       if (SQLconnector.SQLVerify(SQLQuery, password,true)) {
         verify = true;
          } else {
           if (n){
               Conf.Connect();
               Pass(str, false);
           }
           if (SQLconnector.SQLconnetionALIVE() && !n){
               if (SQLconnector.SQLVerify(SQLQuery, password, false)){
                   verify = true;
               }
           }
       }
    /* se nao for = verdade retorna o false declarado acima. */
     return verify;
     }

   private boolean Estado(String user){
       boolean verify = false;
       //Novo select from account_login onde user estado
        String SQLQuery = "SELECT Estado FROM account_login WHERE Nome=\"" + user +"\";";
        if (SQLconnector.SQLVerify(SQLQuery,"" ,true)) {
            verify = true;
        } else {
           
        }
     return verify;
   }

  private int contador(int i){
      i++;
      return i;
  } 
  
  private String CheckLogin(String luser, String lpas) {
      String status = "";
      int Counter = 0;
      if (User(luser,true)) {
          if (Pass(lpas,true)){
              //  verificar logica esta com erro
              if(Estado(luser)) {
                  status = "login";
              } else {
                  status = "error;Your account is lock, please contact this system Administrator.";
              } 
          } else {
              // falta verificar o estado da variavel
              String SQLQuery = "SELECT Contador FROM account_login WHERE Nome=\"" + luser +"\";";
             // String Rturn = SQLconnector.SQLSelect(SQLQuery,0);
              String Rturn = "";
              Counter = Integer.parseInt(Rturn);
              if (contador(Counter /*converter para inteiro*/) < 3) {
                  String SQLInsert = "INSERT INTO account_lock Integer.toString(Counter)";
                  SQLconnector.SQLInsert(SQLInsert);
              } else { 
                  status = "error;User or Password were incorrect. Please try again!";
              }
          } 
      } else {
          status = "error;User or Password were incorrect. Please try again!";
      }
      return status;
  } 
}
