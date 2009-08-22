

class DirectoryController {
    
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST']

    def list = {
        params.max = Math.min( params.max ? params.max.toInteger() : 10,  100)
        [ directoryInstanceList: Directory.list( params ), directoryInstanceTotal: Directory.count() ]
    }

    def show = {
        def directoryInstance = Directory.get( params.id )

        if(!directoryInstance) {
            flash.message = "Directory not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ directoryInstance : directoryInstance ] }
    }

    def delete = {
        def directoryInstance = Directory.get( params.id )
        if(directoryInstance) {
            try {
                directoryInstance.delete(flush:true)
                flash.message = "Directory ${params.id} deleted"
                redirect(action:list)
            }
            catch(org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "Directory ${params.id} could not be deleted"
                redirect(action:show,id:params.id)
            }
        }
        else {
            flash.message = "Directory not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def create = {
        def directoryInstance = new Directory()
        directoryInstance.properties = params
        return ['directoryInstance':directoryInstance]
    }

    def save = {
        def directoryInstance = new Directory(params)
        if(!directoryInstance.hasErrors() && directoryInstance.save()) {
            flash.message = "Directory ${directoryInstance.id} created"
            redirect(action:show,id:directoryInstance.id)
        }
        else {
            render(view:'create',model:[directoryInstance:directoryInstance])
        }
    }

    def scan = {
        // TODO
        redirect(action:show)
    }

}
