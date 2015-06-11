<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Lawrie's house</title>
<style>
#doorcam {
  position: absolute;
  left: 970px;
  top: 10px;
  width: 160px;
  height: 120px;
}

#wanscam {
  position: absolute;
  left: 970px;
  top: 255px;
}

#form {
  position: absolute;
  left: 970px;
  top: 585px;
}

body {
  background: radial-gradient(crimson, deeppink, crimson);
}
</style>
</head>
<body>
<div id="doorcam">
  <img src="http://192.168.0.102:8081" alt="Door Camera Stream"/>
</div>
<div id="wanscam">
  <iframe src="http://admin@192.168.0.99:99/mobile.htm" width="335" height="320"></iframe>
</div>
<iframe src="newdash.php" width="900" height="620"></iframe>
<div id="form">
<form method="post">
Command: <input type="text" name="cmd">
<input value="Go" type="submit">
</form>

<?php

include "funcs.php";

$host = "housecontrol";
$port = 50000;
// No Timeout
set_time_limit(0);


$cmd = $_POST['cmd'];
if (!empty($cmd)) {
  $result = house($host,$port,$cmd);
  echo "Result: $result";
}

?>
</div>
</body>
</html>

