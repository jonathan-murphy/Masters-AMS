<!DOCTYPE html> <html> <body> 
<?php include "../inc/dbinfo.inc"; ?>
<html> <body> <h1>DOWNLOAD DATA</h1> 
<?php /* Connect to MySQL and select  the database. */ 
$connection = mysqli_connect(DB_SERVER, DB_USERNAME,  DB_PASSWORD);  
if (mysqli_connect_errno()) echo ":( Failed to connect to MySQL:  $DB_SERVER " .  mysqli_connect_error(); 
else { echo "CONNECTED TO  DATABASE";} $database = mysqli_select_db($connection, DB_DATABASE); 
/*  INSERT NEW DATA */

$sql = "SELECT Appetite, Mood, Illness, Motivation, Nutrition, Soreness, Stress FROM `mydb`.`Subjective` LIMIT 28";

$subjectiveResult = mysqli_query($connection, $sql);
$subjectiveData = array();

while ($row = mysqli_fetch_array($subjectiveResult))
{
	array_push($subjectiveData,array("appetite"=>$row[0],"mood"=>$row[1],"illness"=>$row[2],"motivation"=>$row[3],"nutrition"=>$row[4],"soreness"=>$row[5],"stress"=>$row[6]));
}

echo json_encode(array("Subjective_data"=>$subjectiveData));

$sql1 = "SELECT Taps, Grip, FT, CT FROM `mydb`.`Test` LIMIT 28";

$testResult = mysqli_query($connection, $sql1);
$testData = array();

  while ($row = mysqli_fetch_array($testResult))
 {
	 array_push($testData,array("taps"=>$row[0],"grip"=>$row[1],"FT"=>$row[2],"CT"=>$row[3]));
 }

 echo json_encode(array("Test_data"=>$testData));

$sql2 = "SELECT Weight, Calories, Protein, Fat, Carbs FROM `mydb`.`Nutrition` LIMIT 28"; 

$nutritionResult = mysqli_query($connection, $sql2);
$nutritionData = array();

  while ($row = mysqli_fetch_array($nutritionResult))
 {
	 array_push($nutritionData,array("weight"=>$row[0],"calories"=>$row[1],"protein"=>$row[2],"fat"=>$row[3],"carbs"=>$row[4]));
 }

 echo json_encode(array("Nutrition_data"=>$nutritionData));
 
?>  
</body> 
</html> 

