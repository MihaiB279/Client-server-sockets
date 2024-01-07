$param4 = $args[0] # No of runs
#Write-Host $param4

# Executare class Java

$suma = 0

for ($i = 0; $i -lt $param4; $i++){
    Write-Host "Rulare" ($i+1)
    $a = ./gradlew runJava
    Write-Host $a[$a.length-1]
    $suma += $a[$a.length-1]

Write-Host " "
}
$media = $suma / $i
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