<?php

class DBOperations{

	 private $host = '127.0.0.1';
	 private $user = 'root';
	 private $db = 'soussi';
	 private $pass = '';
	 private $conn;

public function __construct() {

	$this -> conn = new PDO("mysql:host=".$this -> host.";dbname=".$this -> db, $this -> user, $this -> pass);

}



public function getUser() {

    $sql = 'SELECT * FROM user ';
    $query = $this -> conn -> prepare($sql);
    $query -> execute();
  //  $data = $query -> fetchObject();
    $data = $query -> fetchAll(PDO::FETCH_ASSOC);
   
   //Create File User.json
    $fp = fopen('user.json', 'w');
    fwrite($fp, json_encode($data));
    fclose($fp);


        return $data;
}

public function insertUser($NomUser,$PrenomUser,$CinUser,$ImgUser){

 	
 	$sql = 'INSERT INTO user SET NomUser =:NomUser,PrenomUser =:PrenomUser,
    CinUser =:CinUser,ImgUser =:ImgUser';

 	$query = $this ->conn ->prepare($sql);
 	$query->execute(array(':NomUser' => $NomUser, ':PrenomUser' => $PrenomUser,
     ':CinUser' => $CinUser, ':ImgUser' => $ImgUser));

    if ($query) {
        
        return true;

    } else {

        return false;

    }
 }






}




