<?php

require_once 'db_fun_user.php';

$fun = new Functions();


if ($_SERVER['REQUEST_METHOD'] == 'POST')
{
  $data = json_decode(file_get_contents("php://input"));

  if(isset($_POST['NomUser']) && isset($_POST['PrenomUser']) && isset($_POST['CinUser']) && isset($_POST['ImgUser'])){

  				
  				$NomUser = $_POST['NomUser'];
  				$PrenomUser = $_POST['PrenomUser'];
  				$CinUser =$_POST['CinUser'];
				$ImgUser =$_POST['ImgUser'];

  				
  				

         echo $fun -> addUser($NomUser,$PrenomUser,$CinUser,$ImgUser);


  			} else {

  				echo $fun -> getMsgInvalidParam();

  			}
  
  }

  
 else if ($_SERVER['REQUEST_METHOD'] == 'GET'){

  $data = json_decode(file_get_contents("php://input"));
   



  
  
  
 
        

      

}

