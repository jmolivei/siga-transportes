@echo off

cd C:\DB1\Java\WORKSPACES\WORKSPACE_JAVA_VRAPTOR\siga-transportes

call mvn clean install -DskipTests -Dsiga.versao=1.1.1

if %errorlevel% NEQ 0 (
  echo.
  echo.
  echo.
  echo Erro ao gerar pacote! Saindo...
  goto end
)

call copy /Y sigatp\target\sigatp.war ..\..\..\SERVERS\jboss-eap-6.2.3_Diego\standalone\deployments\

if %errorlevel% NEQ 0 (
  echo.
  echo.
  echo.
  echo Erro ao copiar pacote para o diretorio deployments do JBoss! Saindo...
  goto end
)

echo.
echo.
echo.
echo Projeto publicado com sucesso. Saindo...
goto end


:end
endlocal