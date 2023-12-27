{
  description = "Echemportal BioBrick";

  inputs = { dev-shell.url = "github:biobricks-ai/dev-shell"; };

  outputs = { self, dev-shell }: { devShells = dev-shell.devShells; };
}
