package common;

/*+----------------------------------------------------------------------
 ||
 ||  Class InputValidation
 ||
 ||         Author:  Matthew Song
 ||
 ||        Purpose:  Validation methods for Strings
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