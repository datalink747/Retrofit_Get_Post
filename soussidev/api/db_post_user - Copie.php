<?php

require_once 'db_fun_user.php';

$fun = new Functions();


if ($_SERVER['REQUEST_METHOD'] == 'POST')
{
  $data = json_decode(file_get_contents("php://input"));

  if(isset($data -> user ) && !empty($data -> user) && isset($data -> user -> NomUser) 
  				&& isset($data -> user -> PrenomUser) && isset($data -> user -> CinUser)){

  				$user = $data -> user;
  				$NomUser = $user -> NomUser;
  				$PrenomUser = $user -> PrenomUser;
  				$CinUser = $user -> CinUser;

  				
  				

         echo $fun -> addUser($NomUser,$PrenomUser,$CinUser);


  			} else {

  				echo $fun -> getMsgInvalidParam();

  			}
  
  }

  
 else if ($_SERVER['REQUEST_METHOD'] == 'GET'){

  $data = json_decode(file_get_contents("php://input"));
   



  
  
  
 
        

      

}

