$projRoot = '.\'
$archiveDir = '.\artifacts\'

$cwd = Get-Location
Write-Host $cwd

function Pack-Jars
{
    Get-ChildItem $projRoot -filter "*.jar" -recurse | `
    foreach{
        $targetFile = $archiveDir + $_.FullName.SubString($cwd.Length);
        New-Item -ItemType File -Path $targetFile -Force;
        Copy-Item $_.FullName -destination $targetFile
    }
}

function Unpack-Jars
{
    Get-ChildItem $archiveDir -filter "*.jar" -recurse | `
    foreach{
        $targetFile = "\" + $_.FullName.SubString($cwd.Length);
        New-Item -ItemType File -Path $targetFile -Force;
        Copy-Item $_.FullName -destination $targetFile
    }
}

$param1 = $args[0]

switch ($param1)
{
    "pack" {
        Pack-Jars; break
    }
    "unpack" {
        Unpack-Jars; break
    }
    default {
        "Unknown Command"; break
    }
}

