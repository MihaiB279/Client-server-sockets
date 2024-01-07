$param1 = $args[0] # Nume fisier java
#Write-Host $param1

#Args for Server
$param2 = $args[1] # No of pr threads
#Write-Host $param2

$param3 = $args[2] # No of pw threads
#Write-Host $param3

$param4 = $args[3] # deltaT milisec
#Write-Host $param4

$param5 = $args[4] # No of runs
#Write-Host $param5

# Executare class Java

$suma = 0

for ($i = 0; $i -lt $param5; $i++){
    Write-Host "Rulare" ($i+1)
    $a = java $args[0] $args[1] $args[2] $args[3] # rulare class java
    Write-Host $a[$a.length-1]
    $suma += $a[$a.length-1]
}
$media = $suma / $param5
#Write-Host $suma
Write-Host "Timp de executie mediu:" $media

# Creare fisier .csv
if (!(Test-Path outJ.csv)){
    New-Item outJ.csv -ItemType File
    #Scrie date in csv
    Set-Content outJ.csv 'Tip Matrice,Nr threads,Timp executie'
}

# Append
Add-Content outJ.csv ",$($args[1]),$($media)"