package org.obarcia.gestiontareas.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.obarcia.gestiontareas.components.Language;

/**
 * Constraint validator para DOI.
 * 
 * @author obarcia
 */
public class DoiConstraintValidator implements ConstraintValidator<DoiConstraint, Object>
{
    /**
     * Mensaje de error.
     */
    private String message;
    
    @Override
    public void initialize(DoiConstraint c)
    {
        message = Language.getString(c.message());
    }
    @Override
    public boolean isValid(Object candidate, ConstraintValidatorContext cvc)
    {
        String doi = (String)candidate;
        if (doi != null && doi.length() == 9) {
            if (validateCIF(doi) || validateNIF(doi) || validateNIE(doi)) {
                return true;
            }
        }
        
        cvc.disableDefaultConstraintViolation();
        cvc.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();

        
        return false;
    }
    /**
     * Validar un NIF.
     * @param doi DOI base.
     * @return true si es válido, false en caso contrario.
     */
    private boolean validateNIF(String doi)
    {
        if (doi.matches("^\\d{8}[TRWAGMYFPDXBNJZSQVHLCKE]$")) {
            return (doi.charAt(8) == letraNIF(doi.substring(0,8)));
        }

        return false;
    }
    /**
     * Validar un NIE.
     * @param doi DOI base.
     * @return true si es válido, false en caso contrario.
     */
    private boolean validateNIE(String doi)
    {
        if (doi.length() < 9) return false;

        switch(doi.charAt(0)){
            case 'K':
                return validateNIF('0' + doi.substring(1));
            case 'L':
                return validateNIF('0' + doi.substring(1));
            case 'M':
                return validateNIF('0' + doi.substring(1));
            case 'X':
                return validateNIF('0' + doi.substring(1));
            case 'Y':
                return validateNIF('1' + doi.substring(1));
            case 'Z':
                return validateNIF('2' + doi.substring(1));
        }
        return false;
    }
    /**
     * Validar un CIF.
     * @param doi DOI base.
     * @return true si es válido, false en caso contrario.
     */
    private boolean validateCIF(String doi)
    {
        if (doi.matches("^([PQSWCDFGJNRUV]\\d{7}[A-J])|([ABCDEFGHJNPQRSUVW]00\\d{5}[A-J])|([ABEHCDFGJNRUV]\\d{8})$")) {
            char clave = doi.charAt(0);
            char control = doi.charAt(doi.length() - 1);
            String numero = doi.substring(1, doi.length() - 1);

            int suma = 0;
            boolean par = false;
            for (int i = 0; i < 7; i++) {
                if (par) {
                    suma += Integer.parseInt(numero.substring(i, i + 1));
                } else {
                    int mul = 2 * Integer.parseInt(numero.substring(i, i + 1));
                    if (mul >= 10) {
                        mul -= 9;
                    }
                    suma += mul;
                }
                par = !par;
            }

            String ssuma = "" + suma;
            int total = 10 - Integer.parseInt(ssuma.substring(ssuma.length() - 1));
            if (total == 10) {
                total = 0;
            }

            int ncontrol = 0;
            try {
                return (Integer.parseInt("" + control) == total);
            } catch (Exception e) {
                char[] letras = {'J', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
                return (letras[total] == control);
            }
        }
        return false;
    }
    /**
     * Obtiene el caracter de la letra correspondiente.
     * @param doi DOI base.
     * @return Letra< correspondiente.
     */
    private char letraNIF(String doi)
    {
        switch (doi.charAt(0)) {
            case 'L':
                doi = "0" + doi.substring(1);
                break;
            case 'M':
                doi = "0" + doi.substring(1);
                break;
            case 'X':
                doi = "0" + doi.substring(1);
                break;
            case 'Y':
                doi = "1" + doi.substring(1);
                break;
            case 'Z':
                doi = "2" + doi.substring(1);
                break;
        }
        if (doi.matches("^\\d{8}")) {
            char[] tabla = {'T','R','W','A','G','M','Y','F','P','D','X','B','N','J','Z','S','Q','V','H','L','C','K','E'};
            int num = Integer.valueOf(doi.substring(0, 8)).intValue();
            return tabla[num % 23];
        }
        
        return 0;
    }
}