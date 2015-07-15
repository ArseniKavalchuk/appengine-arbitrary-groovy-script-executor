package com.severn.script.service.command;
/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public interface CommandResult {
    
    Object getResult();
    
    boolean hasError();
    
    Throwable getError();
    
    public static class Builder {
        
        public static CommandResult withResult(final Object result) {
            return new CommandResult() {

                @Override
                public Object getResult() {
                    return result;
                }

                @Override
                public boolean hasError() {
                    return false;
                }

                @Override
                public Throwable getError() {
                    return null;
                }
            };
        }
        
        public static CommandResult withError(final Throwable error) {
            return new CommandResult() {

                @Override
                public Object getResult() {
                    return null;
                }

                @Override
                public boolean hasError() {
                    return true;
                }

                @Override
                public Throwable getError() {
                    return error;
                }
            };
        }
    }
    
}
