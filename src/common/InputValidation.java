package common;

/*+----------------------------------------------------------------------
 ||
 ||  Class DatabaseConnection 
 ||
 ||        Author:  Ethan Huang
 ||
 ||     Purpose:  Validation methods for inputs
 ||
 ||  Inherits From:  None
 ||
 ||     Interfaces:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  None
 ||
 ||  Class Methods:  checkStringBlank(String string) --> boolean
 ||
 ||  Inst. Methods:  None
 ||
 ++-----------------------------------------------------------------------*/

public class InputValidation {
    public static boolean checkStringBlank(String string) {
        return string == null || string.isEmpty();
    }
}