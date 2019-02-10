#!groovy

import jenkins.model.*
import hudson.security.*
import jenkins.security.s2m.AdminWhitelistRule
import hudson.security.csrf.DefaultCrumbIssuer


def instance = Jenkins.getInstance()



def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("admin", "admin") /*or we can hardcode admin,admin */
instance.setSecurityRealm(hudsonRealm)


def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)		/*Disabling read permissions for unauthenticated users */
instance.setAuthorizationStrategy(strategy)
instance.save()

Jenkins.instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false) 

 
if(!Jenkins.instance.isQuietingDown()) {
    def j = Jenkins.instance
    if(j.getCrumbIssuer() == null) {
        j.setCrumbIssuer(new DefaultCrumbIssuer(true))
        j.save()
        println 'CSRF Protection configuration has changed.  Enabled CSRF Protection.'
    }
    else {
        println 'Nothing changed.  CSRF Protection already configured.'
    }
}
else {
    println "Shutdown mode enabled.  Configure CSRF protection SKIPPED."
}
