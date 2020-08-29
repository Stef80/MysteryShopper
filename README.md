# MysteryShopper

<p>Il progetto fa uso di alcune librerie per interagire con Firebase utilizzando i servizi offerti per l'immagazzinamento dei dati, l'autenticazione degli utenti e l'invio di notifiche push tra utenti. Vengono inoltre usate librerie di google maps per la geolocalizzazione.</p>

<h3>le librerie sono :</h3>
<p>- firebase-database:19.3.1</br>
- firebase-analytics:17.4.4</br>
- firebase-auth:19.3.2</br>
- firebase-storage:19.1.1</br>
- firebase-messaging:20.2.4'</br>
- volley:volley:1.1.1</br>
- com.google.android.gms:play-services-maps:17.0.0
</p>

<p>L'applicazione permetta la registrazione di due tipi di utenti, lo shopper che viene ingaggiato, e l'employer che è una società con vari negozi sparsi per il territorio e che necessita di una valutazione dei servizi offerti dai vari negozi.</p>
<h3>Registrazione</h3>
<img src="https://github.com/Stef80/MysteryShopper/blob/master/20200827_175155%5B1%5D.gif" width=150 height=250>
 <p>la registrazione viene effettuata attraverso la compilazione di un apposito form per ciasucn tipo di utenza</p>
 </img>
 <hr>
 <h3>Login</h3>
 <p>Una volta registrato baterà effettuare il login per entrare nella pagina dell'utenza a cui ci si è iscritti</p>
<table cellspacing="2" cellpadding="2" width="400" border="0">
<tbody>
<tr>
<td valign="top" width="200">
 <div>
  <h5>Login come shopper</h5> 
  <img src="https://github.com/Stef80/MysteryShopper/blob/master/20200827_172731%5B1%5D.gif" width=150 height=250/>
 </div>
</td>
<td valign="top" width="200">
 <div>
  <h5>Login come employer</h5>
  <img src="https://github.com/Stef80/MysteryShopper/blob/master/login_employer%5B1%5D.gif" width=150 height=250/>
 </div>
 </td>
</tr>
</tbody>
</table>
<hr>
<h3>Notifiche</h3>
 <p>Quando uno shopper viene scelto per un ingaggio riceve una notifica in cui sono presenti gli estremi per l'ingaggio, inoltre aprendo l'applicazione 
  può vedere una lista di tutte le richieste ricevute e il loro stato</p>
  <img src="https://github.com/Stef80/MysteryShopper/blob/master/notification_shopper%5B1%5D.gif" width=150 height=250/>
 
 <p>Alla ricezione della notifica lo shopper potrà decidere se accettare oppure rifiutare e una volta effettuata la scelta verrà inviata una notifica all'employer che lo ha ingaggiato della propria decisione</p>
   <img src="https://github.com/Stef80/MysteryShopper/blob/master/notification_employer%5B1%5D.gif" width=150 height=250/>
   <hr>
   <h3>Visualizzazione del luogo</h3>
   <p>Lo shopper prima di accettare attraverso google maps può verificare l'ubicazione del luogo </p>
    
<table cellspacing="2" cellpadding="2" width="400" border="0">
<tbody>
<tr>
<td valign="top" width="200">
 <div>
  <img src="https://github.com/Stef80/MysteryShopper/blob/master/maps_visualization%5B1%5D.gif" width=150 height=250>cliccando sulla finestra della proposta</img> </div>
</td>
<td valign="top" width="200">
 <div>
 
  <img src="https://github.com/Stef80/MysteryShopper/blob/master/maps_from_notification%5B1%5D.gif" width=150 height=250>o cliccando sul pulsante show nella finestra della notifica</img>
 </div>
 </td>
</tr>
</tbody>
</table>

