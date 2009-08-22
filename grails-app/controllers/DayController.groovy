

class DayController {
    
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [update:'POST']

    def list = {
        params.max = Math.min( params.max ? params.max.toInteger() : 10,  100)
        [ dayInstanceList: Day.list( params ), dayInstanceTotal: Day.count() ]
    }

    def edit = {
        def dayInstance = Day.get( params.id )

        if(!dayInstance) {
            flash.message = "Day not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ dayInstance : dayInstance ]
        }
    }

    def update = {
        def dayInstance = Day.get( params.id )
        if(dayInstance) {
            if(params.version) {
                def version = params.version.toLong()
                if(dayInstance.version > version) {
                    
                    dayInstance.errors.rejectValue("version", "day.optimistic.locking.failure", "Another user has updated this Day while you were editing.")
                    render(view:'edit',model:[dayInstance:dayInstance])
                    return
                }
            }
            dayInstance.properties = params
            if(!dayInstance.hasErrors() && dayInstance.save()) {
                flash.message = "Day ${params.id} updated"
                redirect(action:show,id:dayInstance.id)
            }
            else {
                render(view:'edit',model:[dayInstance:dayInstance])
            }
        }
        else {
            flash.message = "Day not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def export = {
        // TODO
        redirect(action:edit)
    }

}
