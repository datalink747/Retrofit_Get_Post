<?php

require_once 'db_fun_user.php';

$fun = new Functions();


if ($_SERVER['REQUEST_METHOD'] == 'POST')
{
  $data = json_decode(file_get_contents("php://input"));

   echo $fun -> getUser();
  
  }

  
 else if ($_SERVER['REQUEST_METHOD'] == 'GET'){

  $data = json_decode(file_get_contents("php://input"));
   



  echo $fun -> getUser();
  
  
 
        

      

}

