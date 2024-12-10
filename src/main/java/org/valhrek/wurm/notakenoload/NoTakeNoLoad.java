package org.valhrek.wurm.notakenoload;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.Versioned;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

import java.util.logging.Level;
import java.util.logging.Logger;


public class NoTakeNoLoad implements WurmServerMod, PreInitable, Versioned {

    private static final Logger logger = Logger.getLogger(NoTakeNoLoad.class.getName());

    @Override
    public void preInit() {
        try {
            String code = "$_ = (target.isNoTake() && target.isNoMove()) || $proceed($$);";
            CtClass ctCreature = HookManager.getInstance().getClassPool().get(
                    "com.wurmonline.server.behaviours.CargoTransportationMethods");
            ctCreature.getDeclaredMethod("loadCargo").instrument(new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if ( m.getMethodName().equals("targetIsNotTransportable")) {
                        logger.info("Added no take flag check for " + m.getMethodName());
                        m.replace(code);
                    }
                }
            });
        } catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "failed to update loadCargo to check for no take flag. ", e);
        }
    }
    @Override
    public String getVersion(){
        return "1.0";
    }
}
