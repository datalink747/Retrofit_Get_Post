<?php

require_once 'Functions.php';

$fun = new Functions();


if ($_SERVER['REQUEST_METHOD'] == 'POST')
{
  $data = json_decode(file_get_contents("php://input"));

  if(isset($data -> operation)){

  	$operation = $data -> operation;

  	if(!empty($operation)){

  		if($operation == 'getUser'){ 
		
		
         echo $fun -> getUser();
		 
  		}
		
		else if($operation == 'add_User'){

  			if(isset($data -> user ) && !empty($data -> user) && isset($data -> user -> NomUser) 
  				&& isset($data -> user -> PrenomUser) && isset($data -> user -> CinUser) && isset($data -> user -> ImgUser)){

  				$user = $data -> user;
  				$NomUser = $user -> NomUser;
  				$PrenomUser = $user -> PrenomUser;
  				$CinUser = $user -> CinUser;
                $imgUser = $user -> ImgUser;
  				
  				

         echo $fun -> addUser($NomUser, $PrenomUser, $CinUser, $ImgUser);


  			} else {

  				echo $fun -> getMsgInvalidParam();

  			}

  		}
		
		
	  else {

          echo $fun -> getMsgInvalidParam();

        }

      
	  

  	}else{

  		
  		echo $fun -> getMsgParamNotEmpty();

  	
  } 
  
  }
  else {

  		echo $fun -> getMsgInvalidParam();

  }
  
  }

  
 else if ($_SERVER['REQUEST_METHOD'] == 'GET'){

  $data = json_decode(file_get_contents("php://input"));
   



  echo $fun -> getUser();
  
  
 
        

      

}

