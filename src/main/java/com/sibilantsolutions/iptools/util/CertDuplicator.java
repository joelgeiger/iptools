package com.sibilantsolutions.iptools.util;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

abstract public class CertDuplicator
{

    final static private Logger log = LoggerFactory.getLogger( CertDuplicator.class );

    private CertDuplicator() {} //Prevent instantiation.

    /**
     * Create a self-signed certificate that duplicates as many of the given cert's fields as possible.
     *
     * @param cert
     * @return
     */
    static public X509Certificate duplicate( X509Certificate cert )
    {
        //log.info( "Duplicating cert={}.", cert );

        X500Principal prince = cert.getSubjectX500Principal();

        log.info( "Principal={}.", prince.getName() );

        KeyPairGenerator kpg;
        try
        {
            kpg = KeyPairGenerator.getInstance( "RSA" );
        }
        catch ( NoSuchAlgorithmException e1 )
        {
            // TODO Auto-generated catch block
            throw new UnsupportedOperationException( "OGTE TODO!", e1 );
        }

        KeyPair keyPair = kpg.generateKeyPair();

        X509CertInfo info = new X509CertInfo();
        try
        {
            info.set( X509CertInfo.SUBJECT, new CertificateSubjectName( new X500Name( prince.getName() ) ) );
            info.set( X509CertInfo.KEY, new CertificateX509Key( keyPair.getPublic() ) );
            info.set( X509CertInfo.VALIDITY, new CertificateValidity( cert.getNotBefore(), cert.getNotAfter() ) );
            info.set( X509CertInfo.ISSUER, new CertificateIssuerName( new X500Name( prince.getName() ) ) );
            info.set( X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(
                    new AlgorithmId( new ObjectIdentifier( cert.getSigAlgOID() ) ) ) );
            info.set( X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber( cert.getSerialNumber() ) );
        }
        catch ( CertificateException | IOException e )
        {
            // TODO Auto-generated catch block
            throw new UnsupportedOperationException( "OGTE TODO!", e );
        }

        log.info( "Cert info={}.", info );

        X509CertImpl newCert = new X509CertImpl( info );

        String str = newCert.toString();

        log.info( "Returning new cert={}.", newCert );

        return newCert;
    }

}
