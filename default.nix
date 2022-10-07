{ sources ? import ./nix/sources.nix, pkgs ? import sources.nixpkgs { } }:
with pkgs;
let
  jdk = pkgs.openjdk17;
  csv2parquet = rustPlatform.buildRustPackage rec {
    pname = "csv2parquet";
    version = "0.6.0";

    src = fetchFromGitHub {
      owner = "domoritz";
      repo = pname;
      rev = "v" + version;
      sha256 = "sha256-kb5j7d5lhElbDuoDpsijaXy3Dxjs7nRCUorkg4vKQi8=";
    };

    cargoSha256 = "sha256-rfwqLWNl05GyIBCOv9PaaYmkHBa58x0ck8Jz1qZyeos=";

    meta = with lib; {
      description = "Convert CSV files to Apache Parquet";
      homepage = "https://github.com/domoritz/csv2parquet";
      license = licenses.mit;
    };
  };
in mkShell {
  buildInputs = [
    chromedriver
    chromium
    (clojure.override { jdk = jdk; })
    csv2parquet
    jdk
    rlwrap
  ];
}
