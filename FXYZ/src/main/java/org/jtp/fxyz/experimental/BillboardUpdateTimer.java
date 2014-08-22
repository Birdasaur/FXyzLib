/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jtp.fxyz.experimental;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;

/**
 *
 * @author Dub
 */
public final class BillboardUpdateTimer extends AnimationTimer{
    private final List<Callable<Void>> updates = new ArrayList<>();
    public List<Callable<Void>> getUpdateList(){return updates;}
    public void addUpdate(Callable<Void> c){
        if(updates.size() > 0){
            //return
        }else{
            updates.add(c);
        }
    }
    public void removeUpdate(Callable<Void> c){
        int idx = !updates.isEmpty() ? updates.indexOf(c) : -1;
        if(idx != -1){
            updates.remove(idx);
        }        
    }
    
    public void update(){
        if(!updates.isEmpty()){
            updates.forEach(u ->{ 
                try {
                    u.call();
                } catch (Exception ex) {
                    Logger.getLogger(BillboardUpdateTimer.class.getName()).log(Level.SEVERE, null, ex);
                }
            });            
        }
    }
    @Override
    public void handle(long now) {
        update();
    }
    
}
