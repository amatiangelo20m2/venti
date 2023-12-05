# venti
Venti metri quadri backend service

to update

STEPS INSTALLAZIONE APPLICAZIONE:

Lanciare da terminale il seguente comando:

#docker-compose up -d

Collegarsi alla dashboard keycloak al seguente url

http://localhost:8181/realms/20m2keycloak
(qui stiamo supponendo che l'immegine docker sia stata runnata su porta 8181)
host : http://{HOST}:8181/
username: amati.angelo@20m2.it
password: 5Gv94E4uefK8qm92

NOTA: quay.io/keycloak/keycloak:18.0.0 è l'immagine usata per keycloak, esistono versioni piu aggiornate ma per la guida scritta
di seguito abbiamo bisogno necessariamente della 18.0.0

NOTA: Le utenze sono state configurate tramite il dockercomposer

Creare un nuovo realm con nome: 20m2keycloak

NOTA: importantissimo perchè sull'application.properties dell'api-gw è configurato come:

spring.security.oauth2.resourceserver.jwt.issuer-uri= http://keycloak:8080/realms/20m2keycloak

Crea ora un nuovo Client

clientId: 20m2keycloak

accessType: Confidential

Service Accounts Enabled: ON
Standard Flow Enabled: OFF
Direct Access Grants Enabled: OFF

Copiare il codice segreto dal tab Credentials (sul client che hai creato) #CODICE_SEGRETO

############### Per provarlo su postman
ex: get http://localhost:8080/user-service/getuser?id=1
nel tab Authorization

Token Name: 20m2keycloak
Grant type: Client Credentials
Access Token URL: http://localhost:8181/realms/20m2keycloak/protocol/openid-connect/token (dipende dalla configurazione ovviamente)
ClientId: 20m2keycloak
Client Secret: #CODICE_SEGRETO (copiato dalla dash di keycloak)


############### Per provarlo su postman FINE
