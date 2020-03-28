Param(
[string]$RootDir = ".\",
[string]$ArchiveDir = ".\artifacts\",
[Parameter(Position=0, Mandatory=$true)][string]$Cmd
)
#
#$RootDir = $RootDir.Trimend("\") + "\"
#$ArchiveDir = $ArchiveDir.Trimend("\") + "\"

$cwd = Get-Location
Write-Host "Running from: $cwd"
Write-Host "Root: $RootDir"
Write-Host "Archive: $ArchiveDir"

function Pack-Jars
{
    Get-ChildItem $RootDir -filter "*.jar" -recurse | `
    foreach{
        $targetFile = $ArchiveDir + $_.FullName.SubString($cwd.Length);
        New-Item -ItemType File -Path $targetFile -Force;
        Copy-Item $_.FullName -destination $targetFile
    }
}

function Unpack-Jars
{
    Get-ChildItem "$ArchiveDir" -filter "*.jar" -recurse | `
    foreach{
        $targetFile = "\" + $_.FullName.SubString($cwd.Length);
        New-Item -ItemType File -Path $targetFile -Force;
        Copy-Item $_.FullName -destination $targetFile
    }
}

switch ($Cmd)
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

