<!DOCTYPE html>
<html lang="en">

<head>
  <title>House Control Dashboard</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta http-equiv="refresh" content="10" >
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
</head>
<body>

<?php
$host = "housecontrol";
$port = 50000;
// No Timeout 
set_time_limit(0);

include "funcs.php";
?>

<div class="container-fluid">

  <h1>House Control</h1>
  
  <div class="row">
    <div class="col-sm-6" style="background-color:lavender;">
      <h3>Power</h3>

<?php      
$power = array(
    "Total" => "power",
    "Dish washer" => "dishwasher value",
    "Washer" => "washer value", 
    "Dryer" => "dryer value", 
    "Games" => "xbox value", 
    "Bedroom media" => "bedmedia value",
    "Fridge" => "fridge value");
?>       
      <table class="table">
	    <thead>
	      <tr>
	        <th>Name</th>
	        <th>Value</th>
	      </tr>
	    </thead>
	    <tbody>

<?php	    
foreach ($power as $key => $value) {

  $result = house($host, $port, $value);
  if ($key == "Total") $total = $result;
  else $rest += $result;
  $result = number_format($result,0);
?>	    
	      <tr>
	        <td>
<?php
              echo $key
?>              	        
	        </td>
	        <td>
<?php
              echo $result
?> 	        
	        </td>
	      </tr>
<?php
}
?>
          <tr>
            <td>Other</td>
	        <td>
<?php
              echo $total - $rest
?> 	        
	        </td>
	      </tr>      
	    </tbody>
	  </table> 
	  <h3>Kitchen</h3>
<?php
$kitchen = array(
    "Temperature" => "temperature 2",
    "Light level" => "lightlevel 2",
    "Occupied" => "occupied 2",
    "Dish washer" => "dishwasher status");
?>          
      <table class="table">
	    <thead>
	      <tr>
	        <th>Name</th>
	        <th>Value</th>
	      </tr>
	    </thead>
	    <tbody>
<?php	    
foreach ($kitchen as $key => $value) {
  $result = trim(house($host, $port, $value));
  if (is_numeric($result)) $result = number_format($result,2);
?>    
	      <tr>
	        <td>
<?php
              echo $key
?>              	        
	        </td>
	        <td>
<?php
              echo $result
?> 	        
	        </td>
	      </tr>
<?php
}
?>
	    </tbody>
	  </table> 
      <h3>Master bedroom</h3>
<?php
$masterBedroom = array(
	"Temperature" => "radio temperature",
	"Light level" => "radio lightlevel",
    "Left light" => "world status",
    "Right light" => "periodic status");
?>      
      <table class="table">
	    <thead>
<?php	    
foreach ($masterBedroom as $key => $value) {
  $result = trim(house($host, $port, $value));
  if (is_numeric($result)) $result = number_format($result,2);
?>    
	      <tr>
	        <td>
<?php
              echo $key
?>              	        
	        </td>
	        <td>
<?php
              echo $result
?> 	        
	        </td>
	      </tr>
<?php
}
?>
	    </tbody>
	  </table>
	  <h3>Second Bedroom</h3>
<?php
$secondBedroom = array(
    "Temperature" => "espsecond temperature",
    "Humidity" => "espsecond humidity");
?> 	  
      <table class="table">
	    <thead>
	      <tr>
	        <th>Name</th>
	        <th>Value</th>
	      </tr>
	    </thead>
	    <tbody>
<?php	    
foreach ($secondBedroom as $key => $value) {
  $result = trim(house($host, $port, $value));
  if (is_numeric($result)) $result = number_format($result,2);
?>    
	      <tr>
	        <td>
<?php
              echo $key
?>              	        
	        </td>
	        <td>
<?php
              echo $result
?> 	        
	        </td>
	      </tr>
<?php
}
?>
	    </tbody>
	  </table>
	  <h3>Landing</h3>
<?php      
$landing = array(
    "Temperature" => "land temperature",
    "Humidity" => "humidity 1",
    "Light level" => "land lightlevel",
    "Occupied" => "land occupied");
?>	  
      <table class="table">
	    <thead>
	      <tr>
	        <th>Name</th>
	        <th>Value</th>
	      </tr>
	    </thead>
	    <tbody>
<?php	    
foreach ($landing as $key => $value) {
  $result = trim(house($host, $port, $value));
  if (is_numeric($result)) $result = number_format($result,2);
?>	    
	      <tr>
	        <td>
<?php
              echo $key
?>              	        
	        </td>
	        <td>
<?php
              echo $result
?> 	        
	        </td>
	      </tr>
<?php
}
?>	      
	    </tbody>
	  </table>   
    </div>
    <div class="col-sm-6" style="background-color:lavenderblush;">
      <h3>Living Room</h3>
<?php
$livingRoom = array(
    "Temperature (front)" => "temperature 3",
    "Temperature (back)" => "temperature 2",
    "Light level (front)" => "lightlevel 3",
    "Light level (back)" => "lightlevel 2",
    "Occupied (front)" => "occupied 3",
    "Occupied (back)" => "occupied 2",
    "Front light" => "pollock status",
    "Back light" => "picasso status",
    "Couch weight" => "humidity 8",
    "Camera socket" => "spy status",
    "Middle window" => "switch 3 status",
    "Back window" => "switch 2 status",
    "Table socket" => "coffee status",
    "Computer socket" => "print status",
    "Guitar socket" => "guitar status",
    "Banjolele socket" => "banjolele status",
    "Fan socket" => "fan status",
    "Bench socket" => "bench status",
    "Tablet socket" => "trans status",
    "Enocean button" => "switch 4 status");
 ?>        
      <table class="table">
	    <thead>
	      <tr>
	        <th>Name</th>
	        <th>Value</th>
	      </tr>
	    </thead>
	    <tbody>
<?php	    foreach ($livingRoom as $key => $value) {
  $result = trim(house($host, $port, $value));
  if (is_numeric($result)) $result = number_format($result,2);
?>
	    
	      <tr>
	        <td>
<?php
              echo $key
?>              	        
	        </td>
	        <td>
<?php
              echo $result
?> 	        
	        </td>
	      </tr>
<?php
}
?>	      
	    </tbody>
	  </table>  
      <h3>Dining Room</h3>
<?php      
$diningRoom = array();
?>       
      <table class="table">
	    <thead>
	      <tr>
	        <th>Name</th>
	        <th>Value</th>
	      </tr>
	    </thead>
	    <tbody>
<?php	    
foreach ($diningRoom as $key => $value) {
  $result = trim(house($host, $port, $value));
  if (is_numeric($result)) $result = number_format($result,2);
?>
	    
	      <tr>
	        <td>
<?php
              echo $key
?>              	        
	        </td>
	        <td>
<?php
              echo $result
?> 	        
	        </td>
	      </tr>
<?php
}
?>
	    </tbody>
	  </table>
	  <h3>Bathroom</h3>
<?php      
$bathroom = array(
    "Temperature" => "enoceant temperature");
?>	  
      <table class="table">
	    <thead>
	      <tr>
	        <th>Name</th>
	        <th>Value</th>
	      </tr>
	    </thead>
	    <tbody>
<?php	    
foreach ($bathroom as $key => $value) {
  $result = trim(house($host, $port, $value));
  if (is_numeric($result)) $result = number_format($result,2);
?>    
	      <tr>
	        <td>
<?php
              echo $key
?>              	        
	        </td>
	        <td>
<?php
              echo $result
?> 	        
	        </td>
	      </tr>
<?php
}
?>
	    </tbody>
	  </table>       
      <h3>Utility Room</h3>
<?php      
$utilityRoom = array(
    "Temperature" => "esputil temperature",
    "Pressure" => "esputil pressure");
?>      
      <table class="table">
	    <thead>
	      <tr>
	        <th>Name</th>
	        <th>Value</th>
	      </tr>
	    </thead>
	    <tbody>
<?php	    
foreach ($utilityRoom as $key => $value) {
  $result = trim(house($host, $port, $value));
  if (is_numeric($result)) $result = number_format($result,2);
?>    
	      <tr>
	        <td>
<?php
              echo $key
?>              	        
	        </td>
	        <td>
<?php
              echo $result
?> 	        
	        </td>
	      </tr>
<?php
}
?>
	    </tbody>
	  </table>     
      <h3>Hall</h3>
<?php      
$hall = array(
    "Temperature" => "pihall temperature");
?>
      <table class="table">
	    <thead>
	      <tr>
	        <th>Name</th>
	        <th>Value</th>
	      </tr>
	    </thead>
	    <tbody>
<?php	    
foreach ($hall as $key => $value) {
  $result = trim(house($host, $port, $value));
  if (is_numeric($result)) $result = number_format($result,2);
?>    
	      <tr>
	        <td>
<?php
              echo $key
?>              	        
	        </td>
	        <td>
<?php
              echo $result
?> 	        
	        </td>
	      </tr>
<?php
}
?>
	    </tbody>
	  </table>
	</div>
  </div>
    
</div>
</body>
</html>
